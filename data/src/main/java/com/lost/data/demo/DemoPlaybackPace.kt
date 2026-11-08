package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import kotlin.math.cos
import kotlin.math.hypot

/**
 * Qué tan rápido se reproduce cada tramo del GPX en la demo. Lo que se camina va 10 veces más rápido que en
 * la vida real (ahí pasa la historia: desvíos, vueltas, la guía); el tramo en bus va 40 veces más rápido para
 * que la ruta Catambuco → Universidad no tome minutos de presentación. El motor ve siempre los tiempos reales.
 */
object DemoPlaybackPace {
    const val WALKING_FACTOR = 10.0
    const val VEHICLE_FACTOR = 40.0

    /** Más rápido que esto ya no es caminar (trotar ronda los 2.5 m/s). */
    private const val WALKING_LIMIT_MPS = 2.5
    private const val METERS_PER_DEGREE = 111_195.0
    private const val MILLIS_PER_SECOND = 1000.0

    fun delayMillis(from: GeoPoint, to: GeoPoint): Long {
        val gapMillis = to.timestampMillis - from.timestampMillis
        if (gapMillis <= 0) return 0L
        val speed = metersBetween(from, to) / (gapMillis / MILLIS_PER_SECOND)
        val factor = if (speed > WALKING_LIMIT_MPS) VEHICLE_FACTOR else WALKING_FACTOR
        return (gapMillis / factor).toLong()
    }

    private fun metersBetween(a: GeoPoint, b: GeoPoint): Double {
        val north = (b.latitude - a.latitude) * METERS_PER_DEGREE
        val east = (b.longitude - a.longitude) * METERS_PER_DEGREE * cos(Math.toRadians(a.latitude))
        return hypot(north, east)
    }
}
