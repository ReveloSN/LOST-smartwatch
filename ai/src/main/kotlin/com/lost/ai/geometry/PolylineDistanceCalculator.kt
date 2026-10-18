package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.units.Meters

/** Distancia más corta de un punto a una línea quebrada (la ruta de siempre). */
class PolylineDistanceCalculator {
    fun distanceToPath(point: GeoPoint, path: List<GeoPoint>): Meters {
        if (path.isEmpty()) return Meters(Double.MAX_VALUE)
        val projection = LocalProjection(point)
        val origin = PlanePoint(0.0, 0.0)
        if (path.size == 1) return Meters(projection.toMeters(path.first()).distanceTo(origin))
        val nearest = path.map(projection::toMeters)
            .zipWithNext { a, b -> distanceToSegment(origin, a, b) }
            .min()
        return Meters(nearest)
    }

    private fun distanceToSegment(p: PlanePoint, a: PlanePoint, b: PlanePoint): Double {
        val dx = b.x - a.x
        val dy = b.y - a.y
        val lengthSquared = dx * dx + dy * dy
        if (lengthSquared == 0.0) return p.distanceTo(a)
        val t = (((p.x - a.x) * dx + (p.y - a.y) * dy) / lengthSquared).coerceIn(0.0, 1.0)
        return p.distanceTo(PlanePoint(a.x + t * dx, a.y + t * dy))
    }
}
