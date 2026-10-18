package com.lost.domain.session

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionResult
import com.lost.domain.model.units.Probability

/**
 * Después de "Ya estás en tu ruta", lo que pasó antes (vueltas, retraso) queda perdonado:
 * mientras el usuario siga cerca de su ruta de siempre LOST no vuelve a preguntar por eso.
 * Si se vuelve a salir, la gracia termina y el motor decide de nuevo.
 */
class RecoveryGrace {
    private var isActive = false

    fun start() {
        isActive = true
    }

    fun reset() {
        isActive = false
    }

    fun apply(detection: DetectionResult, distanceToRouteMeters: Double?): DetectionResult {
        if (!isActive) return detection
        if (distanceToRouteMeters == null || distanceToRouteMeters > MonitoringThresholds.GRACE_RADIUS_METERS) {
            isActive = false
            return detection
        }
        return detection.copy(
            probability = Probability.ZERO,
            level = AssistanceLevel.LOW,
            reasons = emptyList(),
            isVoluntaryDetour = false
        )
    }
}
