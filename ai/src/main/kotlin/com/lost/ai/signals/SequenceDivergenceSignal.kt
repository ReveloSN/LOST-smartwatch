package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.sequence.SequenceComparator

/**
 * Cuánto se separa el orden del recorrido de hoy del de siempre (DTW "open-end").
 * Detecta un desvío aunque el punto actual vuelva a quedar cerca de la ruta.
 */
class SequenceDivergenceSignal(
    private val thresholds: DetectionThresholds,
    private val comparator: SequenceComparator
) : DeviationSignal {
    override val kind = SignalKind.SEQUENCE_DIVERGENCE

    override fun measure(context: SignalContext): SignalReading {
        if (!context.hasRoutine || context.path.size < 2) return SignalReading.notApplicable(kind)
        val cost = comparator.compare(context.routinePath, context.path, isPartial = true)
        val score = LinearScore.between(
            cost,
            thresholds.divergenceToleranceMeters,
            thresholds.divergenceSaturationMeters
        )
        return SignalReading(kind, score)
    }
}
