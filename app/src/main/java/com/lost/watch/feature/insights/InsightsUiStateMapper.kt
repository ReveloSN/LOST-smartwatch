package com.lost.watch.feature.insights

import com.lost.domain.metrics.InsightsSummary
import com.lost.watch.R
import java.time.DayOfWeek
import javax.inject.Inject
import kotlin.math.roundToInt

/** Convierte el resumen de métricas en las líneas que ve el usuario (eficacia, eficiencia, falsos positivos). */
class InsightsUiStateMapper @Inject constructor() {
    fun map(summary: InsightsSummary, daysWithTrips: Set<DayOfWeek>) = InsightsUiState(
        weekDays = WORK_WEEK,
        daysWithTrips = daysWithTrips,
        tripsCompleted = summary.tripsCompleted,
        routinesLearned = summary.routinesLearned,
        metrics = metricsOf(summary)
    )

    private fun metricsOf(summary: InsightsSummary): List<MetricLine> = listOf(
        MetricLine(
            R.string.insights_success,
            percent(summary.successRate),
            summary.meetsSuccessGoal,
            R.string.insights_goal_success
        ),
        MetricLine(
            R.string.insights_decision,
            seconds(summary.averageDecisionSeconds),
            summary.meetsDecisionGoal,
            R.string.insights_goal_decision
        ),
        MetricLine(
            R.string.insights_recovery,
            seconds(summary.averageRecoverySeconds),
            summary.meetsRecoveryGoal,
            R.string.insights_goal_recovery
        ),
        MetricLine(
            R.string.insights_false_positives,
            percent(summary.falsePositiveRate),
            summary.meetsFalsePositiveGoal,
            R.string.insights_goal_false_positives
        )
    )

    private fun percent(rate: Double?): MetricValue =
        rate?.let { MetricValue.Percent((it * PERCENT).roundToInt()) } ?: MetricValue.NoData

    private fun seconds(value: Double?): MetricValue = value?.let { MetricValue.Seconds(it) } ?: MetricValue.NoData

    private companion object {
        const val PERCENT = 100
        val WORK_WEEK =
            listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY)
    }
}
