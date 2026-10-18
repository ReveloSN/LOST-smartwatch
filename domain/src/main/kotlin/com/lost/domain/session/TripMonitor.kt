package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.port.ContactReader
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.port.IdGenerator
import com.lost.domain.port.RoutineReader
import com.lost.domain.usecase.EvaluateCurrentTripUseCase
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * El corazón de la integración: cada punto GPS alarga el recorrido, el motor lo evalúa,
 * la sesión publica el resultado y la máquina de estados decide si hay que ayudar.
 * También detecta cuándo el usuario volvió a su ruta o llegó a su destino.
 */
class TripMonitor @Inject constructor(
    private val session: TripSession,
    private val evaluateTrip: EvaluateCurrentTripUseCase,
    private val routineReader: RoutineReader,
    private val guidanceCalculator: GuidanceCalculator,
    private val collaborators: TripMonitorCollaborators
) {
    private var trip = Trip(id = "", points = emptyList())
    private val grace = RecoveryGrace()

    suspend fun run(locations: Flow<GeoPoint>) {
        trip = Trip(collaborators.idGenerator.newId(), emptyList())
        grace.reset()
        locations.collect { point ->
            trip = trip.plus(point)
            onNewPoint(trip, point)
        }
    }

    /** Al terminar: la IA aprende del recorrido (si no fue en un lugar nuevo con destino marcado). */
    suspend fun finish() {
        val finished = trip
        trip = Trip(id = "", points = emptyList())
        if (session.snapshot.value.destination == null) collaborators.learnRoutine(finished)
    }

    private suspend fun onNewPoint(trip: Trip, point: GeoPoint) {
        val destination = session.snapshot.value.destination?.location
        val evaluated = evaluateTrip(trip, destination)
        val routine = evaluated.routineId?.let { routineReader.routine(it) }
        val guidance = guidanceCalculator.guide(point, routine?.path.orEmpty(), destination)
        val detection = grace.apply(evaluated, guidance?.distance?.value?.takeIf { routine != null })
        val target = destination ?: routine?.destination
        val hasArrived = target != null && guidanceCalculator.guide(point, emptyList(), target)
            ?.let { it.distance.value <= MonitoringThresholds.ARRIVAL_METERS } == true
        val wasArrived = session.snapshot.value.hasArrived
        session.publish {
            it.copy(trip = trip, detection = detection, routine = routine, guidance = guidance, hasArrived = hasArrived)
        }
        val hasContact = collaborators.contactReader.observeContact().first() != null
        session.dispatch(AssistanceEvent.DetectionUpdated(detection, hasContact))
        checkRecovery(routine != null, guidance)
        if (hasArrived && !wasArrived) session.dispatch(AssistanceEvent.DestinationReached)
    }

    private fun checkRecovery(hasRoutine: Boolean, guidance: Guidance?) {
        val assistance = session.snapshot.value.assistance
        val isGuiding = assistance == AssistanceState.Guiding || assistance == AssistanceState.LocationShared
        val isBackOnRoute = guidance != null && guidance.distance.value <= MonitoringThresholds.ROUTE_RECOVERED_METERS
        if (isGuiding && hasRoutine && isBackOnRoute) {
            session.dispatch(AssistanceEvent.RouteRecovered)
            grace.start()
        }
    }
}
