package com.lost.watch.integration

import com.lost.ai.LostEngineAssembly
import com.lost.data.demo.DemoScenario
import com.lost.data.demo.GpxRouteReader
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.Destination
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.Trip
import com.lost.domain.model.UserSettings
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.SettingsReader
import com.lost.domain.port.TripRecorder
import com.lost.domain.session.TripMonitor
import com.lost.domain.session.TripMonitorCollaborators
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.EvaluateCurrentTripUseCase
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import com.lost.domain.usecase.TrackMetricEventUseCase
import java.io.File
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Demo de la fase 6 sin emulador: reproduce los GPX de `sample-routes/` por la tubería real
 * (motor de IA → monitor → máquina de estados) y comprueba la historia de cada escenario.
 */
class GpxDemoEndToEndTest {
    private val reader = GpxRouteReader()
    private val assembly = LostEngineAssembly()

    private fun gpx(scenario: DemoScenario): List<GeoPoint> =
        File("../sample-routes/${scenario.assetName}").inputStream().use(reader::read)

    private val normalRoute = gpx(DemoScenario.NORMAL_ROUTINE)

    private val casaU = Routine(
        id = "casa-u",
        name = "Casa → U",
        destinationKind = PlaceKind.UNIVERSITY,
        path = normalRoute,
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
        duration = DurationStatistics(
            5,
            (normalRoute.last().timestampMillis - normalRoute.first().timestampMillis) / 60_000.0,
            2.0
        ),
        timesObserved = 5,
        isConfirmed = true
    )

    private class Story(val session: TripSession, val monitor: TripMonitor) {
        val states = mutableListOf<AssistanceState>()
    }

    private fun story(routines: List<Routine>): Story {
        val session = TripSession()
        val routineReader = object : RoutineReader {
            override fun observeRoutines() = flowOf(routines)

            override suspend fun routines() = routines

            override suspend fun routine(id: String) = routines.firstOrNull { it.id == id }
        }
        val settings =
            SettingsReader {
                flowOf(
                    UserSettings.DEFAULT.copy(thresholds = PersonalThresholds.DEFAULT, sensitivity = Sensitivity.MEDIUM)
                )
            }
        val monitor = TripMonitor(
            session = session,
            evaluateTrip = EvaluateCurrentTripUseCase(assembly.detectionEngine(), routineReader, settings),
            routineReader = routineReader,
            guidanceCalculator = assembly.guidanceCalculator(),
            collaborators = TripMonitorCollaborators(
                contactReader = { flowOf(AuthorizedContact("laura", "Laura", "Hermana", "", true)) },
                idGenerator = { "demo" },
                learnRoutine = noLearning(routineReader)
            )
        )
        return Story(session, monitor)
    }

    /** Aprendizaje que no guarda nada: estas pruebas solo miran la escalada de ayuda. */
    private fun noLearning(routineReader: RoutineReader) = LearnRoutineFromTripUseCase(
        object : TripRecorder {
            override suspend fun record(trip: Trip) = Unit

            override suspend fun deleteAll() = Unit
        },
        routineReader,
        object : com.lost.domain.port.RoutineWriter {
            override suspend fun save(routine: Routine) = Unit

            override suspend fun delete(id: String) = Unit

            override suspend fun deleteAll() = Unit
        },
        { _, _ -> null },
        TrackMetricEventUseCase(
            object : com.lost.domain.port.MetricsTracker {
                override suspend fun track(event: com.lost.domain.metrics.MetricEvent) = Unit
            }
        ) { 0L }
    )

    /** Reproduce el GPX; si LOST pregunta, el usuario toca "Sí, guíame". */
    private suspend fun Story.play(points: List<GeoPoint>, destination: Destination? = null) {
        session.start(destination)
        monitor.run(
            flow {
                points.forEach { point ->
                    emit(point)
                    val state = session.snapshot.value.assistance
                    if (states.lastOrNull() != state) states += state
                    if (state is AssistanceState.AskForHelp) session.dispatch(AssistanceEvent.UserRequestedGuidance)
                }
            }
        )
        states += session.snapshot.value.assistance
    }

    @Test
    fun `ruta de siempre LOST solo observa`() = runTest {
        val story = story(listOf(casaU))
        story.play(gpx(DemoScenario.NORMAL_ROUTINE))
        assertTrue(story.states.all { it == AssistanceState.Calm }, "estados ${story.states}")
        assertTrue(story.session.snapshot.value.hasArrived)
    }

    @Test
    fun `desvio voluntario solo pregunta suave`() = runTest {
        val story = story(listOf(casaU))
        story.play(gpx(DemoScenario.VOLUNTARY_DETOUR))
        assertTrue(story.states.any { it is AssistanceState.SoftAsk }, "estados ${story.states}")
        assertFalse(story.states.any { it is AssistanceState.AskForHelp }, "estados ${story.states}")
    }

    @Test
    fun `calle cerrada detecta el desvio guia de regreso y recupera la ruta`() = runTest {
        val story = story(listOf(casaU))
        story.play(gpx(DemoScenario.DISORIENTATION))
        val askIndex = story.states.indexOfFirst {
            it is AssistanceState.AskForHelp ||
                it is AssistanceState.SharingCountdown
        }
        val guideIndex = story.states.indexOf(AssistanceState.Guiding)
        assertTrue(askIndex >= 0, "nunca preguntó: ${story.states}")
        assertTrue(guideIndex > askIndex, "no guió después de preguntar: ${story.states}")
        assertEquals(AssistanceState.Calm, story.states.last(), "no recuperó la ruta: ${story.states}")
    }

    @Test
    fun `hospital sin rutina pregunta al dar vueltas`() = runTest {
        val points = gpx(DemoScenario.NEW_PLACE)
        val room = Destination("room", "Consultorio 304", points.last(), PlaceKind.HOSPITAL, null)
        val story = story(emptyList())
        story.play(points, room)
        assertTrue(
            story.states.any {
                it is AssistanceState.AskForHelp || it is AssistanceState.Guiding
            },
            "estados ${story.states}"
        )
        assertTrue(story.session.snapshot.value.hasArrived)
    }
}
