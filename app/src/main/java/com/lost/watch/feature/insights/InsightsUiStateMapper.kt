package com.lost.watch.feature.insights

import com.lost.domain.metrics.InsightsSummary
import com.lost.domain.metrics.WeekActivity
import com.lost.watch.R
import java.time.DayOfWeek
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * Convierte el resumen de métricas y la actividad de la semana en lo que ve el usuario: el estado de cada
 * día hábil y las líneas de eficacia, eficiencia y falsos positivos.
 */
class InsightsUiStateMapper @Inject constructor() {
    fun map(summary: InsightsSummary, activity: WeekActivity) = InsightsUiState(
        days = WeekDayLabels.workWeek.map { InsightsDay(it, statusOf(it, activity)) },
        tripsCompleted = summary.tripsCompleted,
        metrics = metricsOf(summary)
    )

    /** Un día con alerta cuenta como desvío aunque el recorrido no se haya terminado de guardar. */
    private fun statusOf(day: DayOfWeek, activity: WeekActivity): WeekDayStatus = when (day) {
        in activity.daysWithDeviation -> WeekDayStatus.DEVIATED
        in activity.daysWithTrips -> WeekDayStatus.ON_ROUTE
        else -> WeekDayStatus.NO_TRIP
    }

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
    }
}
