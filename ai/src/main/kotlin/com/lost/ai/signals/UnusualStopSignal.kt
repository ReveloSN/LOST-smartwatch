package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.GeoPoint
import java.util.concurrent.TimeUnit

/**
 * Se detiene donde normalmente no se detiene. Busca en una sola pasada (O(n)) la parada
 * más larga lejos del inicio y del destino, usando los tiempos reales del GPS.
 */
class UnusualStopSignal(
    private val thresholds: DetectionThresholds,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) : DeviationSignal {
    override val kind = SignalKind.UNUSUAL_STOP

    override fun measure(context: SignalContext): SignalReading {
        val points = context.trip.points
        if (points.size < 2) return SignalReading.notApplicable(kind)
        val longestStopSeconds = longestStopSeconds(points, context.target)
        val score = LinearScore.between(longestStopSeconds, thresholds.stopMinSeconds, thresholds.stopSaturationSeconds)
        return SignalReading(kind, score, DeviationReason.UnusualStop.takeIf { score > 0.0 })
    }

    private fun longestStopSeconds(points: List<GeoPoint>, target: GeoPoint?): Double {
        var anchor = points.first()
        var longest = 0L
        points.drop(1).forEach { point ->
            if (isNear(anchor, point, thresholds.stopRadiusMeters)) {
                if (isUnusualPlace(anchor, points.first(), target)) {
                    longest = maxOf(longest, point.timestampMillis - anchor.timestampMillis)
                }
            } else {
                anchor = point
            }
        }
        return longest.toDouble() / TimeUnit.SECONDS.toMillis(1)
    }

    private fun isUnusualPlace(stop: GeoPoint, start: GeoPoint, target: GeoPoint?): Boolean =
        !isNear(stop, start, thresholds.stopIgnoreNearStartMeters) &&
            (target == null || !isNear(stop, target, thresholds.stopIgnoreNearStartMeters))

    private fun isNear(a: GeoPoint, b: GeoPoint, radius: Double) = distanceCalculator.distance(a, b).value <= radius
}
