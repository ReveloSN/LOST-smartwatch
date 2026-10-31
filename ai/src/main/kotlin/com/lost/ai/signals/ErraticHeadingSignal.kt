package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.BearingCalculator
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.GeoPoint

/**
 * Cambios bruscos de dirección. En cada punto compara el rumbo de llegada con el de salida, cada uno
 * medido sobre `headingChordPoints` tramos (40 m): una esquina real sigue midiendo 90°, pero el temblor
 * del GPS se reduce a la mitad. Recorre los giros con un buffer circular (`ArrayDeque`, O(1) al agregar
 * y quitar) y se queda con la ventana más caótica.
 */
class ErraticHeadingSignal(
    private val thresholds: DetectionThresholds,
    private val bearingCalculator: BearingCalculator = BearingCalculator()
) : DeviationSignal {
    override val kind = SignalKind.ERRATIC_HEADING

    override fun measure(context: SignalContext): SignalReading {
        val turns = turns(context.path)
        if (turns.size < MIN_TURNS) return SignalReading.notApplicable(kind)
        val window = ArrayDeque<Boolean>(thresholds.headingWindowSize)
        var worstRatio = 0.0
        turns.map { it >= thresholds.sharpTurnDegrees }
            .forEach { isSharpTurn ->
                if (window.size == thresholds.headingWindowSize) window.removeFirst()
                window.addLast(isSharpTurn)
                worstRatio = maxOf(worstRatio, window.count { it }.toDouble() / thresholds.headingWindowSize)
            }
        val score = LinearScore.between(worstRatio, thresholds.erraticBaselineRatio, thresholds.erraticSaturationRatio)
        return SignalReading(kind, score, DeviationReason.ErraticDirection.takeIf { score > 0.0 })
    }

    /** Giro en cada punto: rumbo de los `chord` tramos anteriores frente al de los `chord` siguientes. */
    private fun turns(path: List<GeoPoint>): List<Double> {
        val chord = thresholds.headingChordPoints
        return (chord..path.size - 1 - chord).map { i ->
            val arrival = bearingCalculator.bearing(path[i - chord], path[i])
            val departure = bearingCalculator.bearing(path[i], path[i + chord])
            arrival.shortestDifferenceTo(departure).value
        }
    }

    private companion object {
        const val MIN_TURNS = 2
    }
}
