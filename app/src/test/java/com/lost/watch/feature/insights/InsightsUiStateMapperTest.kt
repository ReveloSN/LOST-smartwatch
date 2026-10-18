package com.lost.watch.feature.insights

import com.lost.domain.metrics.InsightsSummary
import java.time.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InsightsUiStateMapperTest {
    @Test
    fun `convierte tasas en porcentajes y marca las metas cumplidas`() {
        val summary = InsightsSummary(0.92, 3.0, 25.0, 0.05, alertsShown = 20, routinesLearned = 1, tripsCompleted = 6)
        val state = InsightsUiStateMapper().map(summary, setOf(DayOfWeek.MONDAY))
        assertEquals(MetricValue.Percent(92), state.metrics[0].value)
        assertEquals(MetricValue.Seconds(3.0), state.metrics[1].value)
        assertTrue(state.metrics.all { it.meetsGoal })
        assertEquals(5, state.weekDays.size)
    }

    @Test
    fun `sin datos no muestra numeros inventados`() {
        val summary = InsightsSummary(null, null, null, null, 0, 0, 0)
        val state = InsightsUiStateMapper().map(summary, emptySet())
        assertTrue(state.metrics.all { it.value == MetricValue.NoData })
    }
}
