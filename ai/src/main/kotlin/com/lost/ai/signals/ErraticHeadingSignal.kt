package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.BearingCalculator
import com.lost.domain.detection.DeviationReason

/**
 * Cambios bruscos de dirección. Recorre los rumbos con un buffer circular
 * (`ArrayDeque`, O(1) al agregar y quitar) y se queda con la ventana más caótica.
 */
class ErraticHeadingSignal(
    private val thresholds: DetectionThresholds,
    private val bearingCalculator: BearingCalculator = BearingCalculator()
) : DeviationSignal {
    override val kind = SignalKind.ERRATIC_HEADING

    override fun measure(context: SignalContext): SignalReading {
        val bearings = bearingCalculator.bearings(context.path)
        if (bearings.size < MIN_BEARINGS) return SignalReading.notApplicable(kind)
        val window = ArrayDeque<Boolean>(thresholds.headingWindowSize)
        var worstRatio = 0.0
        bearings.zipWithNext { a, b -> a.shortestDifferenceTo(b).value >= thresholds.sharpTurnDegrees }
            .forEach { isSharpTurn ->
                if (window.size == thresholds.headingWindowSize) window.removeFirst()
                window.addLast(isSharpTurn)
                worstRatio = maxOf(worstRatio, window.count { it }.toDouble() / thresholds.headingWindowSize)
            }
        val score = LinearScore.between(worstRatio, thresholds.erraticBaselineRatio, thresholds.erraticSaturationRatio)
        return SignalReading(kind, score, DeviationReason.ErraticDirection.takeIf { score > 0.0 })
    }

    private companion object {
        const val MIN_BEARINGS = 3
    }
}
