package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * Promedio de los puntos GPS de los últimos `smoothingSeconds`, ponderado por la inversa del cuadrado
 * de su precisión (el promedio de mínima varianza): un punto de ±30 m pesa 9 veces menos que uno de ±10 m.
 * Sin precisión conocida todos pesan igual. Sin hora creciente (datos sintéticos) no promedia.
 */
internal class GpsWindowAverage(private val smoothingSeconds: Double, private val accuracyFloorMeters: Double) {
    private val recent = ArrayDeque<GeoPoint>()

    fun add(point: GeoPoint): GeoPoint {
        if (recent.isNotEmpty() && point.timestampMillis <= recent.last().timestampMillis) recent.clear()
        recent.addLast(point)
        val windowMillis = (smoothingSeconds * MILLIS_PER_SECOND).toLong()
        while (point.timestampMillis - recent.first().timestampMillis > windowMillis) recent.removeFirst()
        val weights = recent.map(::weightOf)
        val total = weights.sum()
        return GeoPoint(
            latitude = recent.indices.sumOf { recent[it].latitude * weights[it] } / total,
            longitude = recent.indices.sumOf { recent[it].longitude * weights[it] } / total,
            timestampMillis = point.timestampMillis
        )
    }

    private fun weightOf(point: GeoPoint): Double {
        val accuracy = maxOf(point.accuracyMeters ?: accuracyFloorMeters, accuracyFloorMeters)
        return 1.0 / (accuracy * accuracy)
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1000.0
    }
}
