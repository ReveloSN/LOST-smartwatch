package com.lost.ai.sequence

import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.model.GeoPoint
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Dynamic Time Warping con banda de Sakoe-Chiba.
 * Usa solo dos filas de la matriz (`DoubleArray`): O(n·m) de tiempo y O(m) de memoria.
 * Tolera que una persona camine más rápido o más lento en un tramo sin penalizarla.
 */
class DtwSequenceComparator(
    private val bandRatio: Double,
    private val minBand: Int,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) : SequenceComparator {

    override fun compare(reference: List<GeoPoint>, candidate: List<GeoPoint>, isPartial: Boolean): Double {
        if (reference.isEmpty() || candidate.isEmpty()) return Double.MAX_VALUE
        val band = max(minBand, (bandRatio * max(reference.size, candidate.size)).roundToInt())
        var previous = DoubleArray(reference.size) { Double.POSITIVE_INFINITY }
        var current = DoubleArray(reference.size)
        for (i in candidate.indices) {
            current.fill(Double.POSITIVE_INFINITY)
            for (j in max(0, i - band)..min(reference.size - 1, i + band)) {
                val cost = distanceCalculator.distance(candidate[i], reference[j]).value
                current[j] = cost + bestPredecessor(previous, current, i, j)
            }
            previous = current.also { current = previous }
        }
        return finalCost(previous, candidate.size, reference.size, isPartial)
    }

    private fun bestPredecessor(previous: DoubleArray, current: DoubleArray, i: Int, j: Int): Double {
        if (i == 0 && j == 0) return 0.0
        val up = if (i > 0) previous[j] else Double.POSITIVE_INFINITY
        val left = if (j > 0) current[j - 1] else Double.POSITIVE_INFINITY
        val diagonal = if (i > 0 && j > 0) previous[j - 1] else Double.POSITIVE_INFINITY
        return minOf(up, left, diagonal)
    }

    private fun finalCost(lastRow: DoubleArray, n: Int, m: Int, isPartial: Boolean): Double {
        if (!isPartial) return lastRow[m - 1] / max(n, m)
        return lastRow.indices
            .filter { lastRow[it].isFinite() }
            .minOfOrNull { j -> lastRow[j] / max(n, j + 1) }
            ?: Double.MAX_VALUE
    }
}
