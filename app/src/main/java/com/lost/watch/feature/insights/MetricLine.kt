package com.lost.watch.feature.insights

import androidx.annotation.StringRes

/** Una métrica con su valor ya calculado y si cumple la meta del plan. */
data class MetricLine(
    @param:StringRes val label: Int,
    val value: MetricValue,
    val meetsGoal: Boolean,
    @param:StringRes val goal: Int
)
