package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.fixtures.SyntheticWalk
import com.lost.ai.fixtures.TripFixtures
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SignalsTest {
    private val thresholds = DetectionThresholds.DEFAULT
    private val resampler = TrajectoryResampler(20.0)
    private val routine = TripFixtures.casaU()

    private fun context(trip: Trip, progress: Double = 0.0, sensitivity: Sensitivity = Sensitivity.MEDIUM) =
        SignalContext(
            trip = trip,
            path = resampler.resample(trip.points),
            routine = routine,
            routinePath = routine.path,
            target = routine.destination,
            progress = progress,
            sensitivity = sensitivity
        )

    @Test
    fun `fuera de ruta mide la distancia a la ruta de siempre`() {
        val reading = OffRouteSignal(thresholds).measure(context(TripFixtures.voluntaryDetourTrip()))
        assertEquals(1.0, reading.score)
        val reason = reading.reason as DeviationReason.LeftUsualRoute
        assertEquals(400.0, reason.distance.value, 2.0)
    }

    @Test
    fun `en la ruta no hay desvio`() {
        assertEquals(0.0, OffRouteSignal(thresholds).measure(context(TripFixtures.normalRoutineTrip())).score)
    }

    @Test
    fun `tres vueltas a la manzana saturan la senal de bucles`() {
        val trip = SyntheticWalk().to(100.0, 0.0).square(60.0, times = 3).trip()
        val reading = LoopSignal(thresholds).measure(context(trip))
        assertEquals(1.0, reading.score)
        assertTrue((reading.reason as DeviationReason.RepeatedArea).times >= 3)
    }

    @Test
    fun `caminar recto no es un bucle ni es erratico`() {
        val straight = context(SyntheticWalk().to(500.0, 0.0).trip())
        assertEquals(0.0, LoopSignal(thresholds).measure(straight).score)
        assertEquals(0.0, ErraticHeadingSignal(thresholds).measure(straight).score)
    }

    @Test
    fun `dar vueltas cerradas es erratico`() {
        val trip = SyntheticWalk().square(40.0, times = 3).trip()
        assertTrue(ErraticHeadingSignal(thresholds).measure(context(trip)).score > 0.5)
    }

    @Test
    fun `alejarse del destino sube la senal`() {
        val away = MovingAwaySignal(thresholds).measure(context(TripFixtures.voluntaryDetourTrip()))
        val toward = MovingAwaySignal(thresholds).measure(context(TripFixtures.normalRoutineTrip()))
        assertEquals(1.0, away.score)
        assertEquals(0.0, toward.score)
    }

    @Test
    fun `una parada larga lejos del inicio es inusual`() {
        val trip = SyntheticWalk().to(200.0, 0.0).stay(240).to(250.0, 0.0).trip()
        assertEquals(1.0, UnusualStopSignal(thresholds).measure(context(trip)).score)
        val waitingAtHome = SyntheticWalk().stay(240).to(200.0, 0.0).trip()
        assertEquals(0.0, UnusualStopSignal(thresholds).measure(context(waitingAtHome)).score)
    }

    @Test
    fun `el retraso depende de la sensibilidad`() {
        val slowTrip = SyntheticWalk().to(300.0, 0.0).stay(600).trip()
        val high = DelaySignal(thresholds).measure(context(slowTrip, progress = 0.27, sensitivity = Sensitivity.HIGH))
        val low = DelaySignal(thresholds).measure(context(slowTrip, progress = 0.27, sensitivity = Sensitivity.LOW))
        assertTrue(high.score > low.score)
        assertTrue(high.reason is DeviationReason.TakingLonger)
    }

    @Test
    fun `sin rutina las senales de ruta no aplican`() {
        val trip = TripFixtures.normalRoutineTrip()
        val noRoutine = context(trip).copy(routine = null, routinePath = emptyList())
        assertFalse(OffRouteSignal(thresholds).measure(noRoutine).isApplicable)
        assertFalse(DelaySignal(thresholds).measure(noRoutine).isApplicable)
    }
}
