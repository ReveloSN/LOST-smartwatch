package com.lost.ai.geometry

import com.lost.ai.config.DetectionThresholds

/**
 * Cómo se limpia el GPS del reloj antes de remuestrear: cada punto se promedia con los de los últimos
 * [smoothingSeconds] y un punto aislado más rápido que [jumpSpeedMps] se toma como salto de la señal.
 */
data class GpsSmoothing(val smoothingSeconds: Double, val jumpSpeedMps: Double) {
    companion object {
        val DEFAULT = GpsSmoothing(
            DetectionThresholds.DEFAULT.gpsSmoothingSeconds,
            DetectionThresholds.DEFAULT.gpsJumpSpeedMps
        )

        fun from(thresholds: DetectionThresholds) =
            GpsSmoothing(thresholds.gpsSmoothingSeconds, thresholds.gpsJumpSpeedMps)
    }
}
