package com.lost.ai.signals

import com.lost.domain.detection.DeviationReason

/**
 * Lectura de una señal: puntaje entre 0 (normal) y 1 (muy anómalo) y, si aplica,
 * la razón que la explica. Una señal no aplicable (p. ej. retraso sin rutina) no cuenta.
 */
data class SignalReading(
    val kind: SignalKind,
    val score: Double,
    val reason: DeviationReason? = null,
    val isApplicable: Boolean = true
) {
    init {
        require(score in 0.0..1.0) { "El puntaje de una señal debe estar entre 0 y 1: $score" }
    }

    companion object {
        fun notApplicable(kind: SignalKind) = SignalReading(kind, 0.0, isApplicable = false)
    }
}
