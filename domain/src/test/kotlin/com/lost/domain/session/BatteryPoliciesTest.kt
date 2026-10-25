package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionResult
import com.lost.domain.detection.DeviationReason
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Meters
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class BatteryPoliciesTest {
    private fun point(seconds: Int) = GeoPoint(4.6, -74.0, seconds * 1_000L)

    private fun onRoutine() = TripSessionSnapshot.IDLE.copy(
        isMonitoring = true,
        routine = DomainFixtures.routine(),
        detection = DetectionResult.CALM.copy(mode = DetectionModeKind.ROUTINE)
    )

    @Test
    fun `en calma el motor evalua como mucho cada 4 s de GPS`() {
        val throttle = EvaluationThrottle()
        val evaluated = (0..10).filter { throttle.shouldEvaluate(point(it), AssistanceState.Calm) }
        assertEquals(listOf(0, 4, 8), evaluated)
    }

    @Test
    fun `mientras guia evalua cada punto`() {
        val throttle = EvaluationThrottle()
        assertTrue((0..5).all { throttle.shouldEvaluate(point(it), AssistanceState.Guiding) })
    }

    @Test
    fun `con puntos cada 5 s como la demo no se salta ninguno`() {
        val throttle = EvaluationThrottle()
        assertTrue((0..50 step 5).all { throttle.shouldEvaluate(point(it), AssistanceState.Calm) })
    }

    @Test
    fun `el recorrido en memoria no pasa del tope y conserva lo reciente`() {
        var trip = Trip("t", emptyList())
        repeat(1_000) { trip = TripPointsCap.append(trip, point(it), maxPoints = 100) }
        assertTrue(trip.points.size <= 100, "tiene ${trip.points.size}")
        assertEquals(point(999), trip.points.last())
        assertEquals(point(0), trip.points.first())
        assertEquals((950..999).map(::point), trip.points.takeLast(50))
    }

    @Test
    fun `gps relajado en rutina y en calma, atento si algo no cuadra y rapido guiando`() {
        val calm = onRoutine()
        assertEquals(MonitoringThresholds.GPS_RELAXED_MILLIS, LocationCadencePolicy.intervalFor(calm))
        val drifting = calm.copy(
            detection = calm.detection.copy(reasons = listOf(DeviationReason.LeftUsualRoute(Meters(60.0))))
        )
        assertEquals(MonitoringThresholds.GPS_ATTENTIVE_MILLIS, LocationCadencePolicy.intervalFor(drifting))
        val medium = calm.copy(detection = calm.detection.copy(level = AssistanceLevel.MEDIUM))
        assertEquals(MonitoringThresholds.GPS_ATTENTIVE_MILLIS, LocationCadencePolicy.intervalFor(medium))
        val guiding = calm.copy(assistance = AssistanceState.Guiding)
        assertEquals(MonitoringThresholds.GPS_GUIDING_MILLIS, LocationCadencePolicy.intervalFor(guiding))
    }

    @Test
    fun `en un lugar nuevo sin rutina nunca usa el gps relajado`() {
        val newPlace = onRoutine().copy(routine = null)
        assertFalse(LocationCadencePolicy.intervalFor(newPlace) == MonitoringThresholds.GPS_RELAXED_MILLIS)
    }
}
