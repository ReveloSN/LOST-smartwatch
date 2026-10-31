package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * Remuestrea una trayectoria para que tenga un punto cada `spacingMeters` (20 m) de avance.
 * Así dos recorridos se comparan por distancia recorrida y no por la frecuencia del GPS;
 * además los puntos de cuando la persona está quieta, el temblor del GPS y los saltos aislados
 * de la señal desaparecen ([GpsSmoothing]).
 */
class TrajectoryResampler(
    private val spacingMeters: Double,
    private val gps: GpsSmoothing = GpsSmoothing.DEFAULT,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) {
    fun resample(points: List<GeoPoint>): List<GeoPoint> {
        if (points.size < 2) return points
        val accumulator = ResampleAccumulator(spacingMeters, gps, distanceCalculator, points.first())
        for (i in 1 until points.size) accumulator.add(points[i])
        return accumulator.result()
    }

    /** Versión que recuerda el recorrido en curso: cada punto nuevo solo remuestrea el último tramo. */
    fun incremental(): IncrementalTrajectoryResampler = IncrementalTrajectoryResampler(this) {
        ResampleAccumulator(spacingMeters, gps, distanceCalculator, it)
    }
}
