package com.lost.domain.session

import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.port.HapticNotifier
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LocationSignalWatchdogTest {
    private val session = TripSession()
    private val haptics = mutableListOf<HapticPattern>()
    private var pointCount = 0

    private fun TestScope.startWatchdog() {
        session.start()
        val watchdog = LocationSignalWatchdog(session, { testScheduler.currentTime }) { haptics += it }
        backgroundScope.launch { watchdog.run() }
        runCurrent()
    }

    private fun TestScope.receive(accuracy: Double?) {
        pointCount++
        val point = GeoPoint(4.6, -74.08 + pointCount * 1e-4, testScheduler.currentTime, accuracy)
        session.publish { it.copy(trip = Trip("t", listOf(point))) }
        runCurrent()
    }

    @Test
    fun `con puntos confiables nunca avisa`() = runTest {
        startWatchdog()
        repeat(MINUTES * 12) {
            receive(accuracy = 8.0)
            advanceTimeBy(5_000)
        }
        assertFalse(session.snapshot.value.hasWeakLocation)
        assertEquals(emptyList<HapticPattern>(), haptics)
    }

    @Test
    fun `un minuto sin ningun punto avisa y vibra una sola vez`() = runTest {
        startWatchdog()
        advanceTimeBy(66_000)
        assertTrue(session.snapshot.value.hasWeakLocation)
        advanceTimeBy(60_000)
        assertEquals(listOf(HapticPattern.GENTLE), haptics)
    }

    @Test
    fun `solo puntos imprecisos cuentan como ubicacion perdida`() = runTest {
        startWatchdog()
        repeat(14) {
            receive(accuracy = 80.0)
            advanceTimeBy(5_000)
        }
        assertTrue(session.snapshot.value.hasWeakLocation)
    }

    @Test
    fun `al volver una ubicacion confiable el aviso se quita enseguida`() = runTest {
        startWatchdog()
        advanceTimeBy(66_000)
        receive(accuracy = null)
        assertFalse(session.snapshot.value.hasWeakLocation)
    }

    @Test
    fun `despues de llegar al destino no avisa`() = runTest {
        startWatchdog()
        session.publish { it.copy(hasArrived = true) }
        advanceTimeBy(120_000)
        assertFalse(session.snapshot.value.hasWeakLocation)
    }

    private companion object {
        const val MINUTES = 3
    }
}
