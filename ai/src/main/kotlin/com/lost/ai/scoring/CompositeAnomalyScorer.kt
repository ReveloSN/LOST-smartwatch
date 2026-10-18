package com.lost.ai.scoring

import com.lost.ai.config.ScoringWeights
import com.lost.ai.signals.DeviationSignal
import com.lost.ai.signals.SignalContext
import com.lost.ai.signals.SignalGroup
import com.lost.ai.signals.SignalKind
import com.lost.ai.signals.SignalReading
import com.lost.domain.model.units.Probability
import kotlin.math.exp

/**
 * Patrón Composite: agrupa N señales y las trata como una sola medida.
 * - desvío = máximo de las señales de desvío
 * - confusión = media ponderada de las señales de confusión
 * - retraso = máximo de las señales de retraso
 * Luego aplica una regresión logística con los pesos del modo de detección.
 */
class CompositeAnomalyScorer(
    private val signals: List<DeviationSignal>,
    private val confusionWeights: Map<SignalKind, Double>
) {
    fun score(context: SignalContext, weights: ScoringWeights): AnomalyScore {
        val readings = signals.map { it.measure(context) }.filter { it.isApplicable }
        val deviation = maxOf(readings, SignalGroup.DEVIATION)
        val confusion = weightedConfusion(readings)
        val delay = maxOf(readings, SignalGroup.DELAY)
        val z = weights.bias +
            weights.deviation * deviation +
            weights.confusion * confusion +
            weights.delay * delay +
            weights.interaction * deviation * confusion
        return AnomalyScore(Probability.clamped(sigmoid(z)), deviation, confusion, delay, readings)
    }

    private fun maxOf(readings: List<SignalReading>, group: SignalGroup): Double =
        readings.filter { it.kind.group == group }.maxOfOrNull { it.score } ?: 0.0

    private fun weightedConfusion(readings: List<SignalReading>): Double {
        val confusion = readings.filter { it.kind.group == SignalGroup.CONFUSION }
        val totalWeight = confusion.sumOf { weightOf(it.kind) }
        if (totalWeight == 0.0) return 0.0
        return confusion.sumOf { it.score * weightOf(it.kind) } / totalWeight
    }

    private fun weightOf(kind: SignalKind) = confusionWeights[kind] ?: DEFAULT_CONFUSION_WEIGHT

    private fun sigmoid(z: Double) = 1.0 / (1.0 + exp(-z))

    private companion object {
        /** Peso de una señal de confusión nueva que todavía no tiene peso configurado. */
        const val DEFAULT_CONFUSION_WEIGHT = 0.2
    }
}
