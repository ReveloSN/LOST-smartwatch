package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.BearingCalculator
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.GeoPoint
import kotlin.math.abs
import kotlin.math.sign

/**
 * Cambios bruscos de dirección. En cada punto compara el rumbo de llegada con el de salida, cada uno
 * medido sobre `headingChordPoints` tramos (40 m): una esquina real sigue midiendo 90°, pero el temblor
 * del GPS se reduce a la mitad. Como la cuerda abarca varios puntos, el temblor reparte una misma esquina
 * en dos giros bruscos seguidos del mismo sentido: esa racha cuenta como **un** giro. Recorre los giros con
 * un buffer circular (`ArrayDeque`, O(1) al agregar y quitar) y se queda con la ventana más caótica.
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
        newSharpTurns(turns)
            .forEach { isSharpTurn ->
                if (window.size == thresholds.headingWindowSize) window.removeFirst()
                window.addLast(isSharpTurn)
                worstRatio = maxOf(worstRatio, window.count { it }.toDouble() / thresholds.headingWindowSize)
            }
        val score = LinearScore.between(worstRatio, thresholds.erraticBaselineRatio, thresholds.erraticSaturationRatio)
        return SignalReading(kind, score, DeviationReason.ErraticDirection.takeIf { score > 0.0 })
    }

    /** Giro con signo en cada punto: rumbo de los `chord` tramos anteriores frente al de los `chord` siguientes. */
    private fun turns(path: List<GeoPoint>): List<Double> {
        val chord = thresholds.headingChordPoints
        return (chord..path.size - 1 - chord).map { i ->
            val arrival = bearingCalculator.bearing(path[i - chord], path[i])
            val departure = bearingCalculator.bearing(path[i], path[i + chord])
            arrival.signedDifferenceTo(departure).value
        }
    }

    /**
     * Qué puntos cuentan como giro brusco. Una racha de giros bruscos hacia el mismo lado que no es más
     * larga que la cuerda es una sola esquina repartida por el temblor: cuenta una vez. Una racha más
     * larga es dar vueltas en círculo y cuenta completa.
     */
    private fun newSharpTurns(turns: List<Double>): List<Boolean> {
        val counted = turns.map(::isSharp).toMutableList()
        var runStart = 0
        for (i in 1..turns.size) {
            val continuesRun = i < turns.size && counted[i] && counted[i - 1] && sign(turns[i]) == sign(turns[i - 1])
            if (continuesRun) continue
            if (i - runStart in 2..thresholds.headingChordPoints) for (j in runStart + 1 until i) counted[j] = false
            runStart = i
        }
        return counted
    }

    private fun isSharp(turn: Double) = abs(turn) >= thresholds.sharpTurnDegrees

    private companion object {
        const val MIN_TURNS = 2
    }
}
