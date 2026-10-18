package com.lost.ai.scoring

import com.lost.ai.config.DetectionConfig
import com.lost.ai.signals.SignalKind

/**
 * Desvío alto + camina estable (sin bucles, sin giros bruscos, sin paradas) = desvío voluntario.
 * En ese caso LOST solo pregunta suave: "¿Vas a otro lado hoy?".
 */
class VoluntaryDetourDetector(private val config: DetectionConfig) {
    fun isVoluntary(score: AnomalyScore): Boolean {
        val unsteadiness = STEADINESS_SIGNALS.maxOf { score.scoreOf(it) }
        return score.deviation >= config.voluntaryMinDeviation && unsteadiness < config.voluntaryMaxUnsteadiness
    }

    private companion object {
        val STEADINESS_SIGNALS = listOf(SignalKind.LOOP, SignalKind.ERRATIC_HEADING, SignalKind.UNUSUAL_STOP)
    }
}
