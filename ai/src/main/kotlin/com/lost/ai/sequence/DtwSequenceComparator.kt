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
 *
 * El recorrido en curso se compara una vez por punto GPS: en vez de rehacer la matriz, retoma las filas
 * ya calculadas ([DtwRowCache]) y solo agrega las nuevas, así cada punto cuesta O(banda) y no O(n·banda).
 * El último punto se recalcula siempre porque el remuestreo lo deja provisional. El resultado es idéntico.
 */
class DtwSequenceComparator(
    private val bandRatio: Double,
    private val minBand: Int,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) : SequenceComparator {
    private val cache = DtwRowCache(CACHED_ROUTES)

    @Synchronized
    override fun compare(reference: List<GeoPoint>, candidate: List<GeoPoint>, isPartial: Boolean): Double {
        if (reference.isEmpty() || candidate.isEmpty()) return Double.MAX_VALUE
        val band = max(minBand, (bandRatio * max(reference.size, candidate.size)).roundToInt())
        val resumed = cache.resume(reference, candidate, band)
        var previous = resumed?.row?.copyOf() ?: DoubleArray(reference.size) { Double.POSITIVE_INFINITY }
        var current = DoubleArray(reference.size)
        for (i in (resumed?.count ?: 0) until candidate.size) {
            val window = max(0, i - band)..min(reference.size - 1, i + band)
            fillRow(reference, candidate[i], window, Rows(previous, current, isFirst = i == 0))
            previous = current.also { current = previous }
            if (i == candidate.size - 2) {
                cache.save(reference, DtwRowCache.Rows(i + 1, candidate.first(), candidate[i], band, previous.copyOf()))
            }
        }
        return finalCost(previous, candidate.size, reference.size, isPartial)
    }

    /** La fila anterior, la que se está llenando y si es la del primer punto del recorrido. */
    private class Rows(val previous: DoubleArray, val current: DoubleArray, val isFirst: Boolean)

    private fun fillRow(reference: List<GeoPoint>, point: GeoPoint, window: IntRange, rows: Rows) {
        rows.current.fill(Double.POSITIVE_INFINITY)
        for (j in window) {
            val cost = distanceCalculator.distance(point, reference[j]).value
            rows.current[j] = cost + bestPredecessor(rows, j)
        }
    }

    private fun bestPredecessor(rows: Rows, j: Int): Double {
        if (rows.isFirst && j == 0) return 0.0
        val up = if (!rows.isFirst) rows.previous[j] else Double.POSITIVE_INFINITY
        val left = if (j > 0) rows.current[j - 1] else Double.POSITIVE_INFINITY
        val diagonal = if (!rows.isFirst && j > 0) rows.previous[j - 1] else Double.POSITIVE_INFINITY
        return minOf(up, left, diagonal)
    }

    private fun finalCost(lastRow: DoubleArray, n: Int, m: Int, isPartial: Boolean): Double {
        if (!isPartial) return lastRow[m - 1] / max(n, m)
        return lastRow.indices
            .filter { lastRow[it].isFinite() }
            .minOfOrNull { j -> lastRow[j] / max(n, j + 1) }
            ?: Double.MAX_VALUE
    }

    private companion object {
        /** Rutinas que se recuerdan a la vez (pocas personas tienen más que salen del mismo lugar). */
        const val CACHED_ROUTES = 4
    }
}
