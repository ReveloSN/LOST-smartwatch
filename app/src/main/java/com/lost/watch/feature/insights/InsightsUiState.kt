package com.lost.watch.feature.insights

/** 03 · Tu semana: cómo fue cada día hábil, resumen y métricas de éxito. */
data class InsightsUiState(val days: List<InsightsDay>, val tripsCompleted: Int, val metrics: List<MetricLine>) {
    val daysWithTrips: Int get() = days.count { it.status != WeekDayStatus.NO_TRIP }
    val daysWithDeviation: Int get() = days.count { it.status == WeekDayStatus.DEVIATED }
}
