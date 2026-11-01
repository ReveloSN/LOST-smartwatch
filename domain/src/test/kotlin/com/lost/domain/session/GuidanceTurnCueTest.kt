package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Degrees
import com.lost.domain.model.units.Meters
import com.lost.domain.port.GuidanceCalculator
import kotlin.math.atan2
import kotlin.math.hypot
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GuidanceTurnCueTest {
    private val haptics = mutableListOf<HapticPattern>()
    private var now = 0L

    /** Distancia y rumbo planos en metros (0° = norte, 90° = este), sin depender del módulo ai. */
    private val distances = GuidanceCalculator { from, _, to ->
        to?.let {
            val north = (it.latitude - from.latitude) * METERS_PER_DEGREE
            val east = (it.longitude - from.longitude) * METERS_PER_DEGREE
            val bearing = (Math.toDegrees(atan2(east, north)) + FULL_TURN) % FULL_TURN
            Guidance(it, Meters(hypot(north, east)), Degrees(bearing))
        }
    }
    private val cue = GuidanceTurnCue(TripSession(), distances, { haptics += it }) { now }

    /** Camina hacia el norte; el punto de regreso queda en el rumbo [targetBearing]. */
    private fun guiding(targetBearing: Double, assistance: AssistanceState = AssistanceState.Guiding) =
        TripSessionSnapshot.IDLE.copy(
            isMonitoring = true,
            assistance = assistance,
            trip = Trip("t", (0..4).map { GeoPoint(it * STEP_DEGREES, 0.0, it * 5_000L) }),
            guidance = Guidance(GeoPoint(0.0, 0.0), Meters(150.0), Degrees(targetBearing))
        )

    @Test
    fun `caminando hacia el objetivo no vibra`() {
        cue.onSnapshot(guiding(targetBearing = 10.0))
        now += 60_000
        cue.onSnapshot(guiding(targetBearing = 10.0))
        assertEquals(emptyList<HapticPattern>(), haptics)
    }

    @Test
    fun `alejandose del objetivo vibra el giro y no insiste antes de 30 s`() {
        cue.onSnapshot(guiding(targetBearing = 180.0))
        now += 31_000
        cue.onSnapshot(guiding(targetBearing = 180.0))
        now += 10_000
        cue.onSnapshot(guiding(targetBearing = 180.0))
        assertEquals(listOf(HapticPattern.DIRECTION), haptics)
    }

    @Test
    fun `al entrar a la guia no repite la vibracion de la escalada`() {
        cue.onSnapshot(guiding(targetBearing = 180.0))
        assertEquals(emptyList<HapticPattern>(), haptics)
    }

    @Test
    fun `fuera de la guia no vibra`() {
        cue.onSnapshot(guiding(targetBearing = 180.0, assistance = AssistanceState.Calm))
        now += 60_000
        cue.onSnapshot(guiding(targetBearing = 180.0, assistance = AssistanceState.Calm))
        assertEquals(emptyList<HapticPattern>(), haptics)
    }

    private companion object {
        const val METERS_PER_DEGREE = 111_320.0
        const val FULL_TURN = 360.0
        const val STEP_DEGREES = 10.0 / METERS_PER_DEGREE
    }
}
