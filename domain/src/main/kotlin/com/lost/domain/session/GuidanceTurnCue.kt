package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.units.Degrees
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.port.HapticNotifier
import com.lost.domain.port.TimeSource
import javax.inject.Inject

/**
 * Guía sin mirar la pantalla: mientras LOST guía, compara hacia dónde camina la persona (sus últimos metros) con
 * el rumbo al punto de regreso. Si se aparta [MonitoringThresholds.TURN_CUE_DEGREES] o más, vibra el patrón de
 * giro, como mucho cada [MonitoringThresholds.TURN_CUE_REPEAT_MILLIS]. Al entrar a la guía no vibra de nuevo:
 * la escalada ya lo hizo.
 */
class GuidanceTurnCue @Inject constructor(
    private val session: TripSession,
    private val distances: GuidanceCalculator,
    private val hapticNotifier: HapticNotifier,
    private val timeSource: TimeSource
) {
    private var wasGuiding = false
    private var lastCueAt = 0L

    suspend fun run() {
        session.snapshot.collect(::onSnapshot)
    }

    fun onSnapshot(snapshot: TripSessionSnapshot) {
        val isGuiding = snapshot.assistance == AssistanceState.Guiding ||
            snapshot.assistance == AssistanceState.LocationShared
        if (isGuiding && !wasGuiding) lastCueAt = timeSource.nowMillis()
        wasGuiding = isGuiding
        if (!isGuiding) return
        val target = snapshot.guidance?.bearing ?: return
        val heading = walkingHeading(snapshot.trip?.points.orEmpty()) ?: return
        val now = timeSource.nowMillis()
        val isOffCourse = heading.shortestDifferenceTo(target).value >= MonitoringThresholds.TURN_CUE_DEGREES
        if (isOffCourse && now - lastCueAt >= MonitoringThresholds.TURN_CUE_REPEAT_MILLIS) {
            hapticNotifier.play(HapticPattern.DIRECTION)
            lastCueAt = now
        }
    }

    /** Rumbo de la caminata: del último punto que quedó a unos metros atrás hasta el actual. */
    private fun walkingHeading(points: List<GeoPoint>): Degrees? {
        val last = points.lastOrNull() ?: return null
        val earlier = points.asReversed().firstOrNull { isFarEnough(it, last) } ?: return null
        return distances.guide(earlier, emptyList(), last)?.bearing
    }

    private fun isFarEnough(point: GeoPoint, last: GeoPoint): Boolean {
        val meters = distances.guide(point, emptyList(), last)?.distance?.value ?: return false
        return meters >= MonitoringThresholds.HEADING_BASELINE_METERS
    }
}
