package com.lost.watch.feature.insights

/** Valor de una métrica: porcentaje, segundos o "aún sin datos". */
sealed interface MetricValue {
    data class Percent(val value: Int) : MetricValue

    data class Seconds(val value: Double) : MetricValue

    data object NoData : MetricValue
}
