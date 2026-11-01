package com.lost.ai.geometry

import com.lost.ai.config.DetectionThresholds
import com.lost.domain.model.GeoPoint

/**
 * Cómo se limpia el GPS del reloj antes de remuestrear: un punto con precisión reportada peor que
 * [maxAccuracyMeters] se ignora, cada punto se promedia con los de los últimos [smoothingSeconds]
 * (pesando menos los imprecisos, ver [GpsWindowAverage]) y un punto aislado más rápido que
 * [jumpSpeedMps] se toma como salto de la señal.
 */
data class GpsSmoothing(
    val smoothingSeconds: Double,
    val jumpSpeedMps: Double,
    val maxAccuracyMeters: Double = DetectionThresholds.DEFAULT.gpsMaxAccuracyMeters,
    val accuracyFloorMeters: Double = DetectionThresholds.DEFAULT.gpsAccuracyFloorMeters
) {
    /** Sin precisión conocida (GPX, datos sintéticos) el punto se acepta. */
    fun isPrecise(point: GeoPoint): Boolean = point.accuracyMeters?.let { it <= maxAccuracyMeters } ?: true

    companion object {
        val DEFAULT = from(DetectionThresholds.DEFAULT)

        fun from(thresholds: DetectionThresholds) = GpsSmoothing(
            thresholds.gpsSmoothingSeconds,
            thresholds.gpsJumpSpeedMps,
            thresholds.gpsMaxAccuracyMeters,
            thresholds.gpsAccuracyFloorMeters
        )
    }
}
