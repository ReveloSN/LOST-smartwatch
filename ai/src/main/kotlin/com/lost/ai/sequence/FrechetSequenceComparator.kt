package com.lost.ai.sequence

import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.model.GeoPoint
import kotlin.math.max
import kotlin.math.min

/**
 * Distancia de Fréchet discreta ("la correa más corta para pasear al perro").
 * Cumple el mismo contrato que [DtwSequenceComparator], así que son intercambiables (Liskov).
 * Es más estricta: devuelve la peor separación, no la media.
 */
class FrechetSequenceComparator(
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) : SequenceComparator {

    override fun compare(reference: List<GeoPoint>, candidate: List<GeoPoint>, isPartial: Boolean): Double {
        if (reference.isEmpty() || candidate.isEmpty()) return Double.MAX_VALUE
        var previous = DoubleArray(reference.size)
        var current = DoubleArray(reference.size)
        for (i in candidate.indices) {
            for (j in reference.indices) {
                val cost = distanceCalculator.distance(candidate[i], reference[j]).value
                current[j] = max(cost, reachable(previous, current, i, j))
            }
            previous = current.also { current = previous }
        }
        return if (isPartial) previous.min() else previous.last()
    }

    private fun reachable(previous: DoubleArray, current: DoubleArray, i: Int, j: Int): Double = when {
        i == 0 && j == 0 -> 0.0
        i == 0 -> current[j - 1]
        j == 0 -> previous[j]
        else -> min(previous[j], min(previous[j - 1], current[j - 1]))
    }
}
