package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.GridCell
import com.lost.ai.geometry.SpatialGrid
import com.lost.domain.detection.DeviationReason

/**
 * Bucles y regresos al mismo punto. Cada punto cae en una celda de la grilla; volver
 * a una celda después de alejarse cuenta como una nueva pasada ("3 veces por la misma cuadra").
 */
class LoopSignal(private val thresholds: DetectionThresholds) : DeviationSignal {
    override val kind = SignalKind.LOOP

    override fun measure(context: SignalContext): SignalReading {
        val path = context.path
        if (path.size < thresholds.loopMinPointsBetweenPasses) return SignalReading.notApplicable(kind)
        val maxPasses = maxPassesPerCell(SpatialGrid(path.first(), thresholds.loopCellMeters), path)
        val score = LinearScore.between(maxPasses.toDouble(), 1.0, thresholds.loopSaturationPasses.toDouble())
        return SignalReading(kind, score, DeviationReason.RepeatedArea(maxPasses).takeIf { maxPasses > 1 })
    }

    private fun maxPassesPerCell(grid: SpatialGrid, path: List<com.lost.domain.model.GeoPoint>): Int {
        val passes = HashMap<GridCell, Int>()
        path.forEachIndexed { index, point ->
            val previous = grid.visit(index, point)
            val cell = grid.cellOf(point)
            val isNewPass = previous.isEmpty() || index - previous.last() > thresholds.loopMinPointsBetweenPasses
            if (isNewPass) passes[cell] = (passes[cell] ?: 0) + 1
        }
        return passes.values.maxOrNull() ?: 0
    }
}
