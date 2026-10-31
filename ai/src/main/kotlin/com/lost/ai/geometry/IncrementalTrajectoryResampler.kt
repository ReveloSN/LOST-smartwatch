package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * Remuestreo incremental para el recorrido en curso: el recorrido solo crece, así que si llega
 * el mismo recorrido con puntos nuevos al final, se remuestrean solo los tramos nuevos (O(k) en vez
 * de O(n) por punto). Si llega otro recorrido, empieza de cero. El resultado es idéntico al completo.
 */
class IncrementalTrajectoryResampler internal constructor(
    private val full: TrajectoryResampler,
    private val newAccumulator: (GeoPoint) -> ResampleAccumulator
) {
    private var accumulator: ResampleAccumulator? = null
    private var first: GeoPoint? = null
    private var processed: List<GeoPoint> = emptyList()

    @Synchronized
    fun resample(points: List<GeoPoint>): List<GeoPoint> {
        if (points.size < 2) return full.resample(points).also { reset() }
        if (!extendsPrevious(points)) restart(points.first())
        val current = requireNotNull(accumulator)
        for (i in processed.size until points.size) current.add(points[i - 1], points[i])
        processed = points
        return current.result(points.last())
    }

    private fun extendsPrevious(points: List<GeoPoint>): Boolean {
        val count = processed.size
        return accumulator != null && count >= 1 && points.size >= count &&
            points.first() == first && points[count - 1] == processed[count - 1]
    }

    private fun restart(start: GeoPoint) {
        accumulator = newAccumulator(start)
        first = start
        processed = listOf(start)
    }

    private fun reset() {
        accumulator = null
        first = null
        processed = emptyList()
    }
}
