package com.lost.domain.metrics

import com.lost.domain.metrics.MetricEventType.ALERT_DISMISSED_IM_FINE
import com.lost.domain.metrics.MetricEventType.ALERT_SHOWN
import com.lost.domain.metrics.MetricEventType.ASSISTANCE_STARTED
import com.lost.domain.metrics.MetricEventType.ROUTE_RECOVERED
import com.lost.domain.metrics.MetricEventType.ROUTINE_LEARNED
import com.lost.domain.metrics.MetricEventType.TRIP_COMPLETED
import com.lost.domain.metrics.MetricEventType.USER_RESPONDED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class InsightsCalculatorTest {
    private val calculator = InsightsCalculator()

    private fun event(type: MetricEventType, elapsed: Long? = null) = MetricEvent(type, 0L, elapsed)

    @Test
    fun `sin eventos no inventa porcentajes`() {
        val summary = calculator.calculate(emptyList())
        assertNull(summary.successRate)
        assertNull(summary.falsePositiveRate)
        assertNull(summary.averageDecisionSeconds)
        assertEquals(0, summary.alertsShown)
    }

    @Test
    fun `calcula eficacia eficiencia y falsos positivos`() {
        val events = List(10) { event(ALERT_SHOWN) } +
            event(ALERT_DISMISSED_IM_FINE) +
            List(10) { event(ASSISTANCE_STARTED) } +
            List(9) { event(ROUTE_RECOVERED, elapsed = 25_000) } +
            event(USER_RESPONDED, elapsed = 2_000) +
            event(USER_RESPONDED, elapsed = 4_000) +
            event(ROUTINE_LEARNED) +
            List(5) { event(TRIP_COMPLETED) }

        val summary = calculator.calculate(events)

        assertEquals(0.9, summary.successRate!!, 1e-9)
        assertEquals(0.1, summary.falsePositiveRate!!, 1e-9)
        assertEquals(3.0, summary.averageDecisionSeconds!!, 1e-9)
        assertEquals(25.0, summary.averageRecoverySeconds!!, 1e-9)
        assertEquals(1, summary.routinesLearned)
        assertEquals(5, summary.tripsCompleted)
        assertTrue(summary.meetsSuccessGoal)
        assertTrue(summary.meetsDecisionGoal)
        assertTrue(summary.meetsRecoveryGoal)
        assertFalse(summary.meetsFalsePositiveGoal)
    }
}
