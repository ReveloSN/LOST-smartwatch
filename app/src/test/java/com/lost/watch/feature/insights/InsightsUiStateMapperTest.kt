package com.lost.watch.feature.insights

import com.lost.domain.metrics.InsightsSummary
import com.lost.domain.metrics.WeekActivity
import java.time.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InsightsUiStateMapperTest {
    private val emptySummary = InsightsSummary(null, null, null, null, 0, 0, 0)

    @Test
    fun `convierte tasas en porcentajes y marca las metas cumplidas`() {
        val summary = InsightsSummary(0.92, 3.0, 25.0, 0.05, alertsShown = 20, routinesLearned = 1, tripsCompleted = 6)
        val state = InsightsUiStateMapper().map(summary, WeekActivity(setOf(DayOfWeek.MONDAY), emptySet()))
        assertEquals(MetricValue.Percent(92), state.metrics[0].value)
        assertEquals(MetricValue.Seconds(3.0), state.metrics[1].value)
        assertTrue(state.metrics.all { it.meetsGoal })
        assertEquals(5, state.days.size)
    }

    @Test
    fun `sin datos no muestra numeros inventados`() {
        val state = InsightsUiStateMapper().map(emptySummary, WeekActivity.EMPTY)
        assertTrue(state.metrics.all { it.value == MetricValue.NoData })
        assertTrue(state.days.all { it.status == WeekDayStatus.NO_TRIP })
    }

    @Test
    fun `un segmento por dia habil y el dia con desvio marcado`() {
        val activity = WeekActivity(
            daysWithTrips = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
            daysWithDeviation = setOf(DayOfWeek.TUESDAY)
        )
        val state = InsightsUiStateMapper().map(emptySummary, activity)
        val expected = listOf(
            WeekDayStatus.ON_ROUTE,
            WeekDayStatus.DEVIATED,
            WeekDayStatus.ON_ROUTE,
            WeekDayStatus.NO_TRIP,
            WeekDayStatus.NO_TRIP
        )
        assertEquals(expected, state.days.map { it.status })
        assertEquals(3, state.daysWithTrips)
        assertEquals(1, state.daysWithDeviation)
    }

    @Test
    fun `una alerta sin recorrido guardado cuenta como dia con desvio`() {
        val activity = WeekActivity(daysWithTrips = emptySet(), daysWithDeviation = setOf(DayOfWeek.FRIDAY))
        val state = InsightsUiStateMapper().map(emptySummary, activity)
        assertEquals(WeekDayStatus.DEVIATED, state.days.last().status)
        assertEquals(1, state.daysWithTrips)
    }
}
