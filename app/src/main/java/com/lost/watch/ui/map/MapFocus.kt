package com.lost.watch.ui.map

import com.lost.domain.model.GeoPoint
import kotlin.math.cos
import kotlin.math.hypot

/**
 * Qué parte del mapa mirar mientras se camina. Una ruta corta (por el barrio) se ve entera; en una larga
 * (Catambuco → Universidad, ~11 km) el mapa de toda la ciudad esconde el desvío de hoy, que mide unas
 * cuadras. Entonces se mira una ventana fija de 1 km centrada en la persona: el punto actual queda en el
 * centro (lejos del estado de arriba y del texto de abajo) y el zoom no salta con cada punto GPS.
 * `null` = encuadrar todo, como siempre.
 */
object MapFocus {
    private const val LONG_ROUTE_METERS = 3_000.0
    private const val HALF_WINDOW_METERS = 500.0
    private const val METERS_PER_DEGREE = 111_195.0

    fun around(habitual: List<GeoPoint>, today: List<GeoPoint>): List<GeoPoint>? {
        val current = today.lastOrNull() ?: return null
        if (extentMeters(habitual + today) <= LONG_ROUTE_METERS) return null
        val dLat = HALF_WINDOW_METERS / METERS_PER_DEGREE
        val dLon = HALF_WINDOW_METERS / (METERS_PER_DEGREE * cos(Math.toRadians(current.latitude)))
        return listOf(
            GeoPoint(current.latitude + dLat, current.longitude - dLon),
            GeoPoint(current.latitude - dLat, current.longitude + dLon),
            current
        )
    }

    private fun extentMeters(points: List<GeoPoint>): Double {
        val north = (points.maxOf { it.latitude } - points.minOf { it.latitude }) * METERS_PER_DEGREE
        val middle = Math.toRadians(points.first().latitude)
        val east = (points.maxOf { it.longitude } - points.minOf { it.longitude }) * METERS_PER_DEGREE * cos(middle)
        return hypot(north, east)
    }
}
