package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionResult
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeRoutineRepository
import com.lost.domain.fakes.FakeSettingsRepository
import com.lost.domain.fakes.RecordingMetricsTracker
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Degrees
import com.lost.domain.model.units.Meters
import com.lost.domain.port.ContactReader
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.port.TripEvaluator
import com.lost.domain.port.TripRecorder
import com.lost.domain.usecase.EvaluateCurrentTripUseCase
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import com.lost.domain.usecase.TrackMetricEventUseCase
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TripMonitorTest {
    private val session = TripSession()
    private val routines = FakeRoutineRepository(listOf(DomainFixtures.routine()))
    private var nextResult: (Trip) -> DetectionResult = { DomainFixtures.result(AssistanceLevel.LOW) }
    private var guidanceMeters = 200.0
    private val recorded = mutableListOf<Trip>()

    private val monitor = TripMonitor(
        session = session,
        evaluateTrip = EvaluateCurrentTripUseCase(
            TripEvaluator {
                nextResult(it.trip)
            },
            routines,
            FakeSettingsRepository()
        ),
        routineReader = routines,
        guidanceCalculator = GuidanceCalculator { _, _, _ ->
            Guidance(GeoPoint(0.0, 0.0), Meters(guidanceMeters), Degrees(90.0))
        },
        collaborators = TripMonitorCollaborators(
            contactReader = ContactReader { flowOf(DomainFixtures.laura) },
            idGenerator = { "trip" },
            learnRoutine = LearnRoutineFromTripUseCase(
                tripRecorder = object : TripRecorder {
                    override suspend fun record(trip: Trip) {
                        recorded += trip
                    }

                    override suspend fun deleteAll() = Unit
                },
                routineReader = routines,
                routineWriter = routines,
                learningEngine = { _, _ -> null },
                trackMetric = TrackMetricEventUseCase(RecordingMetricsTracker()) { 0L }
            )
        )
    )

    private val points = DomainFixtures.trip(points = 6).points

    @Test
    fun `cada punto alarga el recorrido y publica la evaluacion`() = runTest {
        session.start()
        nextResult = { DomainFixtures.result(AssistanceLevel.MEDIUM) }
        monitor.run(flowOf(*points.toTypedArray()))

        val snapshot = session.snapshot.value
        assertEquals(6, snapshot.trip?.points?.size)
        assertEquals("r1", snapshot.routine?.id)
        assertTrue(snapshot.assistance is AssistanceState.AskForHelp)
    }

    @Test
    fun `al volver cerca de la ruta mientras guia se recupera la ruta`() = runTest {
        session.start()
        nextResult = { DomainFixtures.result(AssistanceLevel.MEDIUM) }
        monitor.run(
            flow {
                points.take(4).forEach { emit(it) }
                session.dispatch(AssistanceEvent.UserRequestedGuidance)
                guidanceMeters = 10.0
                nextResult = { DomainFixtures.result(AssistanceLevel.LOW) }
                emit(points.last())
            }
        )
        assertEquals(AssistanceState.Calm, session.snapshot.value.assistance)
    }

    @Test
    fun `al terminar aprende del recorrido`() = runTest {
        session.start()
        monitor.run(flowOf(*points.toTypedArray()))
        monitor.finish()
        assertEquals(1, recorded.size)
    }
}
