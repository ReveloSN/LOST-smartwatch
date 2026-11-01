package com.lost.watch.integration

import com.lost.ai.LostEngineAssembly
import com.lost.ai.config.DetectionConfig
import com.lost.ai.signals.DeviationSignal
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

/**
 * Reproduce recorridos por la tubería real (motor de IA → monitor → máquina de estados) sin emulador.
 * Lo usan la demo de los GPX y las pruebas de ruido del GPS. [wrapSignals] permite espiar las señales
 * para diagnosticar por qué el motor decidió algo.
 */
class GpxStoryHarness(
    config: DetectionConfig = DetectionConfig.DEFAULT,
    private val wrapSignals: (List<DeviationSignal>) -> List<DeviationSignal> = { it }
) {
    private val reader = GpxRouteReader()
    private val assembly = LostEngineAssembly(config)

    fun gpx(scenario: DemoScenario): List<GeoPoint> =
        File("../sample-routes/${scenario.assetName}").inputStream().use(reader::read)

    private val normalRoute = gpx(DemoScenario.NORMAL_ROUTINE)

    val casaU = Routine(
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

    class Story(val session: TripSession, val monitor: TripMonitor) {
        val states = mutableListOf<AssistanceState>()
    }

    fun story(routines: List<Routine>): Story {
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
            evaluateTrip = EvaluateCurrentTripUseCase(
                assembly.detectionEngine(wrapSignals(assembly.signals())),
                routineReader,
                settings
            ),
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

    /**
     * Reproduce el GPX; si LOST pregunta "¿Necesitas ayuda?", el usuario toca "Sí, guíame". A "¿Vas a otro
     * lado hoy?" responde [softAskAnswer] (por defecto no responde).
     */
    suspend fun Story.play(
        points: List<GeoPoint>,
        destination: Destination? = null,
        softAskAnswer: AssistanceEvent? = null
    ) {
        session.start(destination)
        monitor.run(
            flow {
                points.forEach { point ->
                    emit(point)
                    val state = session.snapshot.value.assistance
                    if (states.lastOrNull() != state) states += state
                    answer(state, softAskAnswer)
                }
            }
        )
        states += session.snapshot.value.assistance
    }

    private fun Story.answer(state: AssistanceState, softAskAnswer: AssistanceEvent?) {
        when {
            state is AssistanceState.AskForHelp -> session.dispatch(AssistanceEvent.UserRequestedGuidance)

            state is AssistanceState.SoftAsk && softAskAnswer != null -> {
                session.dispatch(softAskAnswer)
                // El "Entendido" se cierra solo a los 3 s (en la app lo hace AssistanceTimers).
                session.dispatch(AssistanceEvent.AcknowledgementElapsed)
            }
        }
    }
}
