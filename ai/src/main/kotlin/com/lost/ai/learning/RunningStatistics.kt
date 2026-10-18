package com.lost.ai.learning

import com.lost.domain.model.DurationStatistics

/**
 * Algoritmo de Welford: actualiza media y varianza con cada recorrido nuevo
 * sin guardar la lista completa de duraciones. Numéricamente estable.
 */
class RunningStatistics {
    fun add(current: DurationStatistics, valueMinutes: Double): DurationStatistics {
        val count = current.count + 1
        val delta = valueMinutes - current.meanMinutes
        val mean = current.meanMinutes + delta / count
        val m2 = current.m2 + delta * (valueMinutes - mean)
        return DurationStatistics(count = count, meanMinutes = mean, m2 = m2)
    }
}
