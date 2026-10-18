package com.lost.ai.scoring

import com.lost.ai.signals.SignalKind
import com.lost.ai.signals.SignalReading
import com.lost.domain.model.units.Probability

/** Puntaje combinado: probabilidad final y los tres componentes que la explican. */
data class AnomalyScore(
    val probability: Probability,
    val deviation: Double,
    val confusion: Double,
    val delay: Double,
    val readings: List<SignalReading>
) {
    fun scoreOf(kind: SignalKind): Double = readings.firstOrNull { it.kind == kind && it.isApplicable }?.score ?: 0.0
}
