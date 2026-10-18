package com.lost.watch.feature.insights

import java.time.DayOfWeek

/** 03 · Tu semana: días con recorrido, resumen y métricas de éxito. */
data class InsightsUiState(
    val weekDays: List<DayOfWeek>,
    val daysWithTrips: Set<DayOfWeek>,
    val tripsCompleted: Int,
    val routinesLearned: Int,
    val metrics: List<MetricLine>
)
