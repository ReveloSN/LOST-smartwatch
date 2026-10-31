package com.lost.data.persistence

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lost.data.metrics.LocalMetricsTracker
import com.lost.data.persistence.mapper.DestinationEntityMapper
import com.lost.data.persistence.mapper.MetricEventEntityMapper
import com.lost.data.persistence.mapper.RoutineEntityMapper
import com.lost.data.persistence.mapper.TripEntityMapper
import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.Destination
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Trip
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Room contra el SQLite real del reloj, a través de los repositorios: así se prueban también los
 * mapeadores y los convertidores (trayectorias, días de la semana) que en la JVM no se ejercitan.
 */
@RunWith(AndroidJUnit4::class)
class LostDatabaseTest {
    private lateinit var database: LostDatabase
    private lateinit var routines: RoomRoutineRepository
    private lateinit var trips: RoomTripRepository
    private lateinit var metrics: LocalMetricsTracker
    private lateinit var destinations: RoomDestinationRepository

    @Before
    fun openDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, LostDatabase::class.java).build()
        routines = RoomRoutineRepository(database.routineDao(), RoutineEntityMapper())
        trips = RoomTripRepository(database.tripDao(), TripEntityMapper())
        metrics = LocalMetricsTracker(database.metricEventDao(), MetricEventEntityMapper())
        destinations = RoomDestinationRepository(database.destinationDao(), DestinationEntityMapper())
    }

    @After
    fun closeDatabase() = database.close()

    @Test
    fun unaRutinaGuardadaVuelveIgual() = runTest {
        routines.save(casaU)
        assertEquals(casaU, routines.routine("casa-u"))
        assertEquals(listOf(casaU), routines.observeRoutines().first())
    }

    @Test
    fun guardarConElMismoIdReemplazaYBorrarLaQuita() = runTest {
        routines.save(casaU)
        routines.save(casaU.copy(timesObserved = 6))
        assertEquals(6, routines.routines().single().timesObserved)
        routines.delete("casa-u")
        assertNull(routines.routine("casa-u"))
    }

    @Test
    fun borrarLoAprendidoVaciaRutinasYRecorridos() = runTest {
        routines.save(casaU)
        trips.record(trip("t1", startMillis = MONDAY))
        routines.deleteAll()
        trips.deleteAll()
        assertEquals(emptyList<Routine>(), routines.routines())
        assertEquals(emptyList<Trip>(), trips.tripsSince(0L))
    }

    @Test
    fun losRecorridosDesdeUnaFechaVienenOrdenadosYCompletos() = runTest {
        trips.record(trip("martes", startMillis = MONDAY + DAY))
        trips.record(trip("domingo", startMillis = MONDAY - DAY))
        trips.record(trip("lunes", startMillis = MONDAY))
        val week = trips.tripsSince(MONDAY)
        assertEquals(listOf("lunes", "martes"), week.map { it.id })
        assertEquals(trip("lunes", startMillis = MONDAY).points, week.first().points)
    }

    @Test
    fun lasMetricasDeLaSemanaConservanSusTiempos() = runTest {
        metrics.track(MetricEvent(MetricEventType.ALERT_SHOWN, MONDAY - DAY))
        metrics.track(MetricEvent(MetricEventType.USER_RESPONDED, MONDAY + 2_000, elapsedMillis = 3_400))
        metrics.track(MetricEvent(MetricEventType.ALERT_SHOWN, MONDAY))
        val week = metrics.observeEventsSince(MONDAY).first()
        assertEquals(listOf(MetricEventType.ALERT_SHOWN, MetricEventType.USER_RESPONDED), week.map { it.type })
        assertEquals(listOf(null, 3_400L), week.map { it.elapsedMillis })
    }

    @Test
    fun unDestinoGuardadoConservaSusCoordenadas() = runTest {
        val library = Destination("lib", "Biblioteca Central", GeoPoint(4.6355, -74.0829), PlaceKind.LIBRARY, MONDAY)
        destinations.save(library)
        destinations.save(library.copy(lastUsedAtMillis = MONDAY + DAY))
        assertEquals(listOf(library.copy(lastUsedAtMillis = MONDAY + DAY)), destinations.observeDestinations().first())
    }

    private fun trip(id: String, startMillis: Long) = Trip(
        id,
        List(POINTS) { i -> GeoPoint(4.6321 + i * STEP_DEGREES, -74.0759, startMillis + i * 5_000L) }
    )

    private companion object {
        const val MONDAY = 1_791_812_400_000L
        const val DAY = 86_400_000L
        const val POINTS = 4
        const val STEP_DEGREES = 0.00006

        val casaU = Routine(
            id = "casa-u",
            name = "Casa → U",
            destinationKind = PlaceKind.UNIVERSITY,
            path = listOf(GeoPoint(4.6321, -74.0759, 1L), GeoPoint(4.6380, -74.0840, 2L)),
            schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
            duration = DurationStatistics(5, 17.2, 2.0),
            timesObserved = 5,
            isConfirmed = true
        )
    }
}
