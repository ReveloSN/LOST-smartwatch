package com.lost.ai.learning

import com.lost.ai.LostEngineAssembly
import com.lost.ai.fixtures.SyntheticWalk
import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.Routine
import java.time.DayOfWeek
import kotlin.math.sqrt
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNotEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class LearningTest {
    private var nextId = 0
    private val learner = LostEngineAssembly().routineLearner { "r${nextId++}" }

    @Test
    fun `welford coincide con el calculo directo`() {
        val values = listOf(33.0, 35.0, 36.0, 34.0, 40.0)
        val stats = values.fold(DurationStatistics.EMPTY) { acc, v -> RunningStatistics().add(acc, v) }
        val mean = values.average()
        val std = sqrt(values.sumOf { (it - mean) * (it - mean) } / (values.size - 1))
        assertEquals(mean, stats.meanMinutes, 1e-9)
        assertEquals(std, stats.standardDeviationMinutes, 1e-9)
    }

    @Test
    fun `cinco repeticiones confirman la rutina`() {
        var routines = emptyList<Routine>()
        repeat(5) { day ->
            val trip = SyntheticWalk(startMillis = TripFixtures.MONDAY_6_40 + day * DAY_MILLIS)
                .to(600.0, 0.0).to(600.0, 500.0).trip("t$day")
            val learned = learner.learn(trip, routines)!!
            routines = routines.filterNot { it.id == learned.id } + learned
            assertEquals(day == 4, learned.isConfirmed, "repetición ${day + 1}")
        }
        val routine = routines.single()
        assertEquals(5, routine.timesObserved)
        assertEquals(5, routine.schedule.days.size)
        assertTrue(DayOfWeek.FRIDAY in routine.schedule.days)
        assertEquals(13.1, routine.duration.meanMinutes, 0.5)
    }

    @Test
    fun `un recorrido distinto crea otra rutina`() {
        val known = listOf(TripFixtures.casaU())
        val gym = SyntheticWalk().to(-500.0, 0.0).to(-500.0, -300.0).trip()
        val learned = learner.learn(gym, known)!!
        assertNotEquals("casa-u", learned.id)
        assertFalse(learned.isConfirmed)
    }

    @Test
    fun `el emparejador elige la rutina mas parecida que sale del mismo lugar`() {
        val gym = TripFixtures.casaU().copy(
            id = "gym",
            path = SyntheticWalk().to(0.0, 400.0).points()
        )
        val trip = TripFixtures.voluntaryDetourTrip()
        val matcher = RoutineMatcherProbe()
        assertEquals("casa-u", matcher.best(trip.points, listOf(gym, TripFixtures.casaU())))
    }

    private companion object {
        const val DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}
