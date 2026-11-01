package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * Promedio de los puntos GPS de los últimos segundos, ponderado por la inversa del cuadrado de su
 * precisión (el promedio de mínima varianza): un punto de ±30 m pesa 9 veces menos que uno de ±10 m.
 * Cuando el GPS es impreciso la ventana abarca más segundos ([GpsSmoothing.windowSecondsFor]): entre
 * edificios altos, un desvío tranquilo deja de parecer un ir y venir. Sin precisión conocida todos pesan
 * igual y la ventana es fija. Sin hora creciente (datos sintéticos) no promedia.
 */
internal class GpsWindowAverage(private val gps: GpsSmoothing) {
    private val recent = ArrayDeque<GeoPoint>()

    fun add(point: GeoPoint): GeoPoint {
        if (recent.isNotEmpty() && point.timestampMillis <= recent.last().timestampMillis) recent.clear()
        recent.addLast(point)
        val windowMillis = (gps.windowSecondsFor(point) * MILLIS_PER_SECOND).toLong()
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
        val accuracy = maxOf(point.accuracyMeters ?: gps.accuracyFloorMeters, gps.accuracyFloorMeters)
        return 1.0 / (accuracy * accuracy)
    }

    private companion object {
        const val MILLIS_PER_SECOND = 1000.0
    }
}
