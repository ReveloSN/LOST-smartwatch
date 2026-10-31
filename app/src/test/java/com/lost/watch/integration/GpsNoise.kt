package com.lost.watch.integration

import com.lost.domain.model.GeoPoint
import kotlin.math.cos
import kotlin.random.Random

/**
 * Ruido de GPS reproducible (misma semilla, mismo recorrido) para probar el motor con datos reales de reloj:
 * error gaussiano en cada punto, saltos aislados por rebote de señal (multitrayecto) y puntos que no llegan.
 */
data class GpsNoise(
    val name: String,
    val sigmaMeters: Double,
    val spikeShare: Double = 0.0,
    val spikeMeters: Double = 0.0,
    val dropShare: Double = 0.0,
    val seed: Int = SEED
) {
    fun apply(points: List<GeoPoint>): List<GeoPoint> {
        val random = Random(seed)
        return points.filterIndexed { index, _ ->
            // Nunca se pierden el primero ni el último: el recorrido empieza y termina donde debe.
            index == 0 || index == points.lastIndex || random.nextDouble() >= dropShare
        }.map { point ->
            val spike = if (random.nextDouble() < spikeShare) spikeMeters else 0.0
            val angle = random.nextDouble() * 2 * Math.PI
            point.shifted(
                north = random.nextGaussian() * sigmaMeters + spike * kotlin.math.sin(angle),
                east = random.nextGaussian() * sigmaMeters + spike * cos(angle)
            )
        }
    }

    private fun Random.nextGaussian(): Double {
        // Box-Muller: dos uniformes → una normal estándar.
        val u = 1.0 - nextDouble()
        val v = nextDouble()
        return kotlin.math.sqrt(-2.0 * kotlin.math.ln(u)) * cos(2 * Math.PI * v)
    }

    private fun GeoPoint.shifted(north: Double, east: Double) = copy(
        latitude = latitude + north / METERS_PER_DEGREE,
        longitude = longitude + east / (METERS_PER_DEGREE * cos(Math.toRadians(latitude)))
    )

    companion object {
        private const val METERS_PER_DEGREE = 111_320.0
        private const val SEED = 27

        /** Variantes realistas para un reloj al aire libre en ciudad (Fused Location, sin Wi-Fi). */
        val REALISTIC = listOf(
            GpsNoise("cielo abierto (σ 5 m)", sigmaMeters = 5.0),
            GpsNoise("ciudad (σ 10 m)", sigmaMeters = 10.0),
            GpsNoise("ciudad con rebotes (σ 10 m, 3 % saltos de 40 m)", 10.0, spikeShare = 0.03, spikeMeters = 40.0),
            GpsNoise("puntos perdidos (σ 8 m, 20 % sin llegar)", 8.0, dropShare = 0.2)
        )
    }
}
