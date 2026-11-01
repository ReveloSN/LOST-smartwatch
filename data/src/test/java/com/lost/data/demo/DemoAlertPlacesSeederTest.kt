package com.lost.data.demo

import com.lost.domain.model.AlertPlace
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.port.AlertPlaceRecorder
import com.lost.domain.port.RoutineReader
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DemoAlertPlacesSeederTest {
    private val saved = mutableListOf<AlertPlace>()
    private val recorder = object : AlertPlaceRecorder {
        override suspend fun record(place: AlertPlace) {
            saved += place
        }

        override suspend fun deleteAll() = saved.clear()
    }
    private val routine = Routine(
        "r",
        "Casa → U",
        PlaceKind.UNIVERSITY,
        emptyList(),
        RoutineSchedule(RoutineSchedule.WEEKDAYS, 400),
        DurationStatistics(5, 35.0, 2.0),
        5,
        true
    )
    private val routines = object : RoutineReader {
        override fun observeRoutines() = flowOf(listOf(routine))

        override suspend fun routines() = listOf(routine)

        override suspend fun routine(id: String) = routine
    }
    private val gpx = DemoGpxLoader({ File("../sample-routes/$it").inputStream() }, GpxRouteReader())

    private fun seeder(existing: List<AlertPlace> = emptyList()) =
        DemoAlertPlacesSeeder(DemoAlertPlaceStore({ existing }, recorder), routines, { NOW }, gpx)

    @Test
    fun `siembra tres lugares de hace mas de una semana`() = runTest {
        seeder().seedIfNeeded(isDemo = true)
        assertEquals(3, saved.size)
        assertTrue(saved.all { NOW - it.timestampMillis > TimeUnit.DAYS.toMillis(7) })
        assertEquals(2, saved.count { it.routineName == "Casa → U" })
    }

    @Test
    fun `no siembra si ya hay lugares o si no es la demo`() = runTest {
        seeder(existing = listOf(AlertPlace(gpx.points(DemoScenario.NORMAL_ROUTINE).first(), NOW, null, null)))
            .seedIfNeeded(isDemo = true)
        seeder().seedIfNeeded(isDemo = false)
        assertEquals(emptyList<AlertPlace>(), saved)
    }

    private companion object {
        const val NOW = 1_800_000_000_000L
    }
}
