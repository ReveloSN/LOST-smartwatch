package com.lost.ai.sequence

import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.model.GeoPoint

/** ¿En qué parte de la ruta de siempre va el usuario? Devuelve un avance entre 0 y 1. */
class RouteProgressLocator(
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) {
    fun progress(current: GeoPoint, routePath: List<GeoPoint>): Double {
        if (routePath.size < 2) return 0.0
        val nearestIndex = routePath.indices.minBy { distanceCalculator.distance(current, routePath[it]).value }
        return nearestIndex.toDouble() / (routePath.size - 1)
    }
}
