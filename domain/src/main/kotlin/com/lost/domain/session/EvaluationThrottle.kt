package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.model.GeoPoint

/**
 * Límite de frecuencia del motor: en calma evalúa como mucho una vez cada
 * [MonitoringThresholds.MIN_EVALUATION_INTERVAL_MILLIS] (según la hora del GPS, no del reloj);
 * si LOST está preguntando o guiando, evalúa cada punto para reaccionar al instante.
 */
class EvaluationThrottle(private val minIntervalMillis: Long = MonitoringThresholds.MIN_EVALUATION_INTERVAL_MILLIS) {
    private var lastEvaluatedAt: Long? = null

    fun shouldEvaluate(point: GeoPoint, assistance: AssistanceState): Boolean {
        val last = lastEvaluatedAt
        val isDue = last == null ||
            assistance != AssistanceState.Calm ||
            point.timestampMillis - last >= minIntervalMillis
        if (isDue) lastEvaluatedAt = point.timestampMillis
        return isDue
    }

    fun reset() {
        lastEvaluatedAt = null
    }
}
