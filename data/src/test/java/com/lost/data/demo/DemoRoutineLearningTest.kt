package com.lost.data.demo

import android.content.Context
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Trip
import com.lost.domain.port.RoutineLearningEngine
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import com.lost.domain.port.TripRecorder
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import com.lost.domain.usecase.TrackMetricEventUseCase
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.yield
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DemoRoutineLearningTest {
    private val gpx = """
        <gpx><trk><trkseg>
          <trkpt lat="1.1664" lon="-77.2993"><time>2026-10-12T11:40:00Z</time></trkpt>
          <trkpt lat="1.2000" lon="-77.2950"><time>2026-10-12T12:00:00Z</time></trkpt>
          <trkpt lat="1.2325" lon="-77.2934"><time>2026-10-12T12:15:00Z</time></trkpt>
        </trkseg></trk></gpx>
    """.trimIndent()

    /** Rutinas en memoria; leerlas cede el turno, como una base de datos real, para que se noten las carreras. */
    private class RoutineStore :
        RoutineReader,
        RoutineWriter {
        val saved = mutableMapOf<String, Routine>()

        override fun observeRoutines() = flowOf(saved.values.toList())

        override suspend fun routines(): List<Routine> {
            yield()
            return saved.values.toList()
        }

        override suspend fun routine(id: String) = saved[id]

        override suspend fun save(routine: Routine) {
            saved[routine.id] = routine
        }

        override suspend fun delete(id: String) {
            saved.remove(id)
        }

        override suspend fun deleteAll() = saved.clear()
    }

    /** Aprendizaje mínimo: refuerza la rutina que sale del mismo punto o crea una nueva. */
    private val engine = RoutineLearningEngine { trip, known ->
        val start = trip.points.first()
        val same = known.firstOrNull { it.path.first().latitude == start.latitude && it.id == HABITUAL_ID }
        val times = (same?.timesObserved ?: 0) + 1
        (same ?: routine(HABITUAL_ID, trip.points)).copy(timesObserved = times, isConfirmed = times >= 5)
    }

    private val store = RoutineStore()
    private val learnedTrips = mutableListOf<String>()

    private fun learning(): DemoRoutineLearning {
        val context = mockk<Context> { every { getString(any()) } returns "Casa → U" }
        val noTrips = object : TripRecorder {
            override suspend fun record(trip: Trip) {
                learnedTrips += trip.id
            }

            override suspend fun deleteAll() = Unit
        }
        val learn =
            LearnRoutineFromTripUseCase(
                noTrips,
                store,
                store,
                engine,
                TrackMetricEventUseCase(mockk(relaxed = true)) {
                    0L
                }
            )
        val repositories = DemoRepositories(store, store, mockk(), mockk(), mockk())
        return DemoRoutineLearning(
            context,
            DemoGpxLoader({ gpx.byteInputStream() }, GpxRouteReader()),
            learn,
            repositories,
            { NOW }
        )
    }

    @Test
    fun `dos toques seguidos aprenden dos dias distintos`() = runTest {
        val demo = learning()
        listOf(async { demo.learnNextDay() }, async { demo.learnNextDay() }).awaitAll()
        assertEquals(listOf("demo-day-0", "demo-day-1"), learnedTrips)
        assertEquals(2, demo.learnedDays())
    }

    @Test
    fun `aprender la semana confirma la rutina y le pone nombre`() = runTest {
        val demo = learning()
        listOf(async { demo.learnWholeWeek() }, async { demo.learnNextDay() }).awaitAll()
        val routine = store.saved.getValue(HABITUAL_ID)
        assertEquals(5, routine.timesObserved)
        assertTrue(routine.isConfirmed)
        assertEquals("Casa → U", routine.name)
    }

    @Test
    fun `otra rutina que ya existia no cuenta como la de la demo`() = runTest {
        val elsewhere = listOf(GeoPoint(4.6, -74.08, 0L), GeoPoint(4.61, -74.07, 60_000L))
        store.save(routine("otra", elsewhere).copy(timesObserved = 9, isConfirmed = true))
        assertEquals(0, learning().learnedDays())
    }

    private fun routine(id: String, path: List<GeoPoint>) = Routine(
        id = id,
        name = "",
        destinationKind = PlaceKind.OTHER,
        path = path,
        schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
        duration = DurationStatistics.EMPTY,
        timesObserved = 0,
        isConfirmed = false
    )

    private companion object {
        const val HABITUAL_ID = "demo"
        const val NOW = 1_794_000_000_000L
    }
}
