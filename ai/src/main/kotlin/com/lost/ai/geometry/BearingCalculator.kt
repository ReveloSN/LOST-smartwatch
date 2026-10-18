package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.units.Degrees
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/** Rumbo inicial (0 = norte, 90 = este) para ir de un punto a otro. */
class BearingCalculator {
    fun bearing(from: GeoPoint, to: GeoPoint): Degrees {
        val lat1 = Math.toRadians(from.latitude)
        val lat2 = Math.toRadians(to.latitude)
        val dLon = Math.toRadians(to.longitude - from.longitude)
        val y = sin(dLon) * cos(lat2)
        val x = cos(lat1) * sin(lat2) - sin(lat1) * cos(lat2) * cos(dLon)
        return Degrees(Math.toDegrees(atan2(y, x))).normalized()
    }

    /** Rumbos entre cada par de puntos consecutivos. */
    fun bearings(points: List<GeoPoint>): List<Degrees> = points.zipWithNext { a, b -> bearing(a, b) }
}
