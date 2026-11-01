package com.lost.domain.metrics

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DeviationCause
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.model.AlertPlace
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Trip
import com.lost.domain.model.units.Degrees
import com.lost.domain.model.units.Meters
import com.lost.domain.port.AlertPlaceRecorder
import com.lost.domain.port.DestinationReader
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.session.Guidance
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.GetDisorientationPlacesUseCase
import kotlin.math.hypot
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DisorientationPlacesTest {
    /** Distancia plana en metros: suficiente para las pruebas, sin depender del módulo ai. */
    private val distances = GuidanceCalculator { from, _, destination ->
        destination?.let {
            val north = (it.latitude - from.latitude) * METERS_PER_DEGREE
            val east = (it.longitude - from.longitude) * METERS_PER_DEGREE
            Guidance(it, Meters(hypot(north, east)), Degrees(0.0))
        }
    }

    private fun at(northMeters: Double, eastMeters: Double = 0.0) =
        GeoPoint(northMeters / METERS_PER_DEGREE, eastMeters / METERS_PER_DEGREE)

    private fun alert(location: GeoPoint, time: Long, cause: DeviationCause?, routine: String? = "Casa → U") =
        AlertPlace(location, time, cause, routine)

    @Test
    fun `las alertas a menos de una cuadra son el mismo lugar`() {
        val groups = AlertPlaceGrouper(distances).group(
            listOf(
                alert(at(0.0), 1, DeviationCause.REPEATED_AREA),
                alert(at(60.0), 2, DeviationCause.REPEATED_AREA),
                alert(at(500.0), 3, DeviationCause.LEFT_ROUTE)
            )
        )
        assertEquals(listOf(2, 1), groups.map { it.size })
    }

    @Test
    fun `el nombre es el destino guardado mas cercano o la rutina sin usar internet`() {
        val labeler = PlaceLabeler(distances)
        val library = Destination("d1", "Biblioteca Central", at(200.0), PlaceKind.UNIVERSITY, null)
        assertEquals(
            PlaceLabel.NearDestination("Biblioteca Central"),
            labeler.label(listOf(alert(at(0.0), 1, null)), listOf(library))
        )
        assertEquals(
            PlaceLabel.OnRoutine("Casa → U"),
            labeler.label(listOf(alert(at(2_000.0), 1, null)), listOf(library))
        )
        assertEquals(PlaceLabel.Unknown, labeler.label(listOf(alert(at(0.0), 1, null, routine = null)), emptyList()))
    }

    @Test
    fun `muestra primero el lugar con mas alertas y su causa mas frecuente`() = runTest {
        val alerts = listOf(
            alert(at(0.0), 1, DeviationCause.LEFT_ROUTE),
            alert(at(1_000.0), 2, DeviationCause.REPEATED_AREA),
            alert(at(1_020.0), 3, DeviationCause.REPEATED_AREA),
            alert(at(990.0), 4, DeviationCause.ERRATIC_DIRECTION)
        )
        val places = GetDisorientationPlacesUseCase(
            reader = { since -> alerts.filter { it.timestampMillis >= since } },
            destinations = object : DestinationReader {
                override fun observeDestinations() = flowOf(emptyList<Destination>())
            },
            summarizer = DisorientationPlaceSummarizer(AlertPlaceGrouper(distances), PlaceLabeler(distances)),
            timeSource = { 10L }
        )()
        assertEquals(listOf(3, 1), places.map { it.times })
        assertEquals(DeviationCause.REPEATED_AREA, places.first().mainCause)
    }

    @Test
    fun `al preguntar guarda donde estaba y por que`() = runTest {
        val session = TripSession()
        val saved = mutableListOf<AlertPlace>()
        val recorder = object : AlertPlaceRecorder {
            override suspend fun record(place: AlertPlace) {
                saved += place
            }

            override suspend fun deleteAll() = saved.clear()
        }
        val logger = AlertPlaceLogger(session, recorder) { 99L }
        val trip = Trip("t", listOf(at(10.0)))
        val detection = DomainFixtures.result(AssistanceLevel.MEDIUM)
        session.publish { it.copy(trip = trip, detection = detection) }
        val asked = session.dispatch(AssistanceEvent.DetectionUpdated(detection, true))
        logger.onTransition(asked)
        logger.onTransition(AssistanceTransition(asked.to, asked.to, AssistanceEvent.CountdownTicked))
        logger.onTransition(
            AssistanceTransition(AssistanceState.Calm, AssistanceState.Guiding, AssistanceEvent.UserRequestedGuidance)
        )
        assertEquals(1, saved.size)
        assertEquals(99L, saved.single().timestampMillis)
        assertTrue(saved.single().cause != null)
    }

    private companion object {
        const val METERS_PER_DEGREE = 111_320.0
    }
}
