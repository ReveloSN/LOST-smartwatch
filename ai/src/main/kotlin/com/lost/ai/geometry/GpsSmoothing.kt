package com.lost.ai.geometry

import com.lost.ai.config.DetectionThresholds
import com.lost.domain.model.GeoPoint

/**
 * Cómo se limpia el GPS del reloj antes de remuestrear: un punto con precisión reportada peor que
 * [maxAccuracyMeters] se ignora, cada punto se promedia con los de los últimos [smoothingSeconds]
 * (pesando menos los imprecisos, ver [GpsWindowAverage]) y un punto aislado más rápido que
 * [jumpSpeedMps] se toma como salto de la señal. Con precisión peor que [accuracyReferenceMeters]
 * la ventana crece en proporción, hasta [maxSmoothingFactor] veces.
 */
data class GpsSmoothing(
    val smoothingSeconds: Double,
    val jumpSpeedMps: Double,
    val maxAccuracyMeters: Double = DetectionThresholds.DEFAULT.gpsMaxAccuracyMeters,
    val accuracyFloorMeters: Double = DetectionThresholds.DEFAULT.gpsAccuracyFloorMeters,
    val accuracyReferenceMeters: Double = DetectionThresholds.DEFAULT.gpsAccuracyReferenceMeters,
    val maxSmoothingFactor: Double = DetectionThresholds.DEFAULT.gpsMaxSmoothingFactor
) {
    /** Sin precisión conocida (GPX, datos sintéticos) el punto se acepta. */
    fun isPrecise(point: GeoPoint): Boolean = point.accuracyMeters?.let { it <= maxAccuracyMeters } ?: true

    /** Segundos que abarca el promedio para un punto: más cuanto menos preciso es el GPS. */
    fun windowSecondsFor(point: GeoPoint): Double {
        val factor = (point.accuracyMeters ?: 0.0) / accuracyReferenceMeters
        return smoothingSeconds * factor.coerceIn(1.0, maxSmoothingFactor)
    }

    companion object {
        val DEFAULT = from(DetectionThresholds.DEFAULT)

        fun from(thresholds: DetectionThresholds) = GpsSmoothing(
            thresholds.gpsSmoothingSeconds,
            thresholds.gpsJumpSpeedMps,
            thresholds.gpsMaxAccuracyMeters,
            thresholds.gpsAccuracyFloorMeters,
            thresholds.gpsAccuracyReferenceMeters,
            thresholds.gpsMaxSmoothingFactor
        )
    }
}
