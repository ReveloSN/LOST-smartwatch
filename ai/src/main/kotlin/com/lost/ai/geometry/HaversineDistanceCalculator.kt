package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.units.Meters
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

/** Distancia sobre la esfera terrestre entre dos puntos GPS (fórmula de Haversine). */
class HaversineDistanceCalculator {
    fun distance(from: GeoPoint, to: GeoPoint): Meters {
        val dLat = Math.toRadians(to.latitude - from.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(from.latitude)) * cos(Math.toRadians(to.latitude)) * sin(dLon / 2) * sin(dLon / 2)
        return Meters(2 * EARTH_RADIUS_METERS * asin(sqrt(a.coerceIn(0.0, 1.0))))
    }

    companion object {
        const val EARTH_RADIUS_METERS = 6_371_000.0
    }
}
