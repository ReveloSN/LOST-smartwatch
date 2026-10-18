package com.lost.domain.metrics

import com.lost.domain.metrics.MetricEventType.ALERT_DISMISSED_GOING_ELSEWHERE
import com.lost.domain.metrics.MetricEventType.ALERT_DISMISSED_IM_FINE
import com.lost.domain.metrics.MetricEventType.ALERT_SHOWN
import com.lost.domain.metrics.MetricEventType.ASSISTANCE_STARTED
import com.lost.domain.metrics.MetricEventType.ROUTE_RECOVERED
import com.lost.domain.metrics.MetricEventType.ROUTINE_LEARNED
import com.lost.domain.metrics.MetricEventType.TRIP_COMPLETED
import com.lost.domain.metrics.MetricEventType.USER_RESPONDED
import javax.inject.Inject

/**
 * Convierte eventos crudos en las métricas de la sección 9.4:
 * eficacia, eficiencia (tiempo de decisión y de recuperación), falsos positivos y uso.
 */
class InsightsCalculator @Inject constructor() {
    fun calculate(events: List<MetricEvent>): InsightsSummary {
        val counts = events.groupingBy { it.type }.eachCount()
        fun count(type: MetricEventType) = counts[type] ?: 0
        val alerts = count(ALERT_SHOWN)
        val dismissed = count(ALERT_DISMISSED_IM_FINE) + count(ALERT_DISMISSED_GOING_ELSEWHERE)
        return InsightsSummary(
            successRate = ratio(count(ROUTE_RECOVERED), count(ASSISTANCE_STARTED)),
            averageDecisionSeconds = averageSeconds(events, USER_RESPONDED),
            averageRecoverySeconds = averageSeconds(events, ROUTE_RECOVERED),
            falsePositiveRate = ratio(dismissed, alerts),
            alertsShown = alerts,
            routinesLearned = count(ROUTINE_LEARNED),
            tripsCompleted = count(TRIP_COMPLETED)
        )
    }

    private fun ratio(part: Int, total: Int): Double? =
        if (total == 0) null else (part.toDouble() / total).coerceAtMost(1.0)

    private fun averageSeconds(events: List<MetricEvent>, type: MetricEventType): Double? = events
        .filter { it.type == type }
        .mapNotNull { it.elapsedMillis }
        .takeIf { it.isNotEmpty() }
        ?.average()
        ?.div(MILLIS_PER_SECOND)

    private companion object {
        const val MILLIS_PER_SECOND = 1000.0
    }
}
