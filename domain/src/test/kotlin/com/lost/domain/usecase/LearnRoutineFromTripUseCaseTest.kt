package com.lost.domain.usecase

import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeRoutineRepository
import com.lost.domain.fakes.RecordingMetricsTracker
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.Trip
import com.lost.domain.port.RoutineLearningEngine
import com.lost.domain.port.TripRecorder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LearnRoutineFromTripUseCaseTest {
    private val recorded = mutableListOf<Trip>()
    private val recorder = object : TripRecorder {
        override suspend fun record(trip: Trip) {
            recorded += trip
        }

        override suspend fun deleteAll() = recorded.clear()
    }
    private val routines = FakeRoutineRepository(listOf(DomainFixtures.routine(isConfirmed = false)))
    private val metrics = RecordingMetricsTracker()

    private fun useCase(engine: RoutineLearningEngine) =
        LearnRoutineFromTripUseCase(recorder, routines, routines, engine, TrackMetricEventUseCase(metrics) { 1L })

    @Test
    fun `guarda el recorrido y la rutina aprendida`() = runTest {
        val learned = DomainFixtures.routine(isConfirmed = false).copy(timesObserved = 4)
        useCase { _, _ -> learned }(DomainFixtures.trip())

        assertEquals(1, recorded.size)
        assertEquals(4, routines.routine("r1")!!.timesObserved)
        assertEquals(listOf(MetricEventType.TRIP_COMPLETED), metrics.types)
    }

    @Test
    fun `cuando la rutina se confirma registra rutina aprendida`() = runTest {
        useCase { _, _ -> DomainFixtures.routine(isConfirmed = true) }(DomainFixtures.trip())
        assertTrue(MetricEventType.ROUTINE_LEARNED in metrics.types)
    }
}
