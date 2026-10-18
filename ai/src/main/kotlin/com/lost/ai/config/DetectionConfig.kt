package com.lost.ai.config

import com.lost.ai.signals.SignalKind

/** Configuración completa del motor: umbrales, pesos por modo y peso de cada señal de confusión. */
data class DetectionConfig(
    val thresholds: DetectionThresholds,
    val routineWeights: ScoringWeights,
    val newPlaceWeights: ScoringWeights,
    val confusionWeights: Map<SignalKind, Double>,
    val voluntaryMinDeviation: Double,
    val voluntaryMaxUnsteadiness: Double
) {
    companion object {
        val DEFAULT = DetectionConfig(
            thresholds = DetectionThresholds.DEFAULT,
            routineWeights = ScoringWeights.ROUTINE,
            newPlaceWeights = ScoringWeights.NEW_PLACE,
            confusionWeights = mapOf(
                SignalKind.LOOP to 0.35,
                SignalKind.ERRATIC_HEADING to 0.3,
                SignalKind.UNUSUAL_STOP to 0.25,
                SignalKind.MOVING_AWAY to 0.1
            ),
            voluntaryMinDeviation = 0.5,
            voluntaryMaxUnsteadiness = 0.2
        )
    }
}
