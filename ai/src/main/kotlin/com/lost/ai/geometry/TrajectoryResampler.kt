package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * Remuestrea una trayectoria para que tenga un punto cada `spacingMeters` (20 m).
 * Así dos recorridos se comparan por distancia recorrida y no por la frecuencia del GPS;
 * además los puntos repetidos de cuando la persona está quieta desaparecen.
 */
class TrajectoryResampler(
    private val spacingMeters: Double,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) {
    fun resample(points: List<GeoPoint>): List<GeoPoint> {
        if (points.size < 2) return points
        val result = mutableListOf(points.first())
        var carried = 0.0
        for (i in 1 until points.size) {
            val from = points[i - 1]
            val to = points[i]
            val segment = distanceCalculator.distance(from, to).value
            var position = spacingMeters - carried
            while (position <= segment) {
                result += interpolate(from, to, position / segment)
                position += spacingMeters
            }
            carried = (carried + segment) % spacingMeters
        }
        if (carried > spacingMeters / 2) result += points.last()
        return result
    }

    private fun interpolate(from: GeoPoint, to: GeoPoint, fraction: Double) = GeoPoint(
        latitude = from.latitude + (to.latitude - from.latitude) * fraction,
        longitude = from.longitude + (to.longitude - from.longitude) * fraction,
        timestampMillis = from.timestampMillis + ((to.timestampMillis - from.timestampMillis) * fraction).toLong()
    )
}
