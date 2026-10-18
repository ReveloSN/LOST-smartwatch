package com.lost.watch.ui

import androidx.compose.ui.geometry.Offset
import com.lost.domain.model.GeoPoint
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.max

/**
 * Proyecta la ruta de siempre y la de hoy a un cuadro 0..1 conservando la proporción,
 * y marca el primer punto de hoy que se separa más de `divergenceMeters` de la ruta.
 */
class RouteProjector(private val divergenceMeters: Double = DEFAULT_DIVERGENCE_METERS) {
    fun project(habitual: List<GeoPoint>, today: List<GeoPoint>): ProjectedRoutes {
        val all = habitual + today
        if (all.isEmpty()) return ProjectedRoutes(emptyList(), emptyList(), null)
        val plane = PlaneFrame(all)
        val habitualMeters = habitual.map(plane::toMeters)
        val todayMeters = today.map(plane::toMeters)
        val divergence = todayMeters.firstOrNull { point ->
            habitualMeters.isNotEmpty() &&
                nearest(point, habitualMeters) > divergenceMeters
        }
        return ProjectedRoutes(
            habitual = habitualMeters.map(plane::normalize),
            today = todayMeters.map(plane::normalize),
            divergence = divergence?.let(plane::normalize)
        )
    }

    private fun nearest(point: Offset, path: List<Offset>): Double {
        if (path.size == 1) return distance(point, path.first())
        return path.zipWithNext { a, b -> distanceToSegment(point, a, b) }.min()
    }

    private fun distanceToSegment(p: Offset, a: Offset, b: Offset): Double {
        val dx = (b.x - a.x).toDouble()
        val dy = (b.y - a.y).toDouble()
        val lengthSquared = dx * dx + dy * dy
        if (lengthSquared == 0.0) return distance(p, a)
        val t = (((p.x - a.x) * dx + (p.y - a.y) * dy) / lengthSquared).coerceIn(0.0, 1.0)
        return hypot(p.x - (a.x + t * dx), p.y - (a.y + t * dy))
    }

    private fun distance(a: Offset, b: Offset): Double = hypot((a.x - b.x).toDouble(), (a.y - b.y).toDouble())

    /** Marco plano en metros que contiene todos los puntos, con margen. */
    private class PlaneFrame(points: List<GeoPoint>) {
        private val originLat = points.minOf { it.latitude }
        private val originLon = points.minOf { it.longitude }
        private val lonScale = cos(Math.toRadians(originLat))
        private val maxX = points.maxOf { (it.longitude - originLon) * METERS_PER_DEGREE * lonScale }
        private val maxY = points.maxOf { (it.latitude - originLat) * METERS_PER_DEGREE }
        private val span = max(max(maxX, maxY), MIN_SPAN_METERS)

        fun toMeters(point: GeoPoint) = Offset(
            ((point.longitude - originLon) * METERS_PER_DEGREE * lonScale).toFloat(),
            ((point.latitude - originLat) * METERS_PER_DEGREE).toFloat()
        )

        fun normalize(meters: Offset): Offset {
            val usable = 1f - 2 * MARGIN
            val x = MARGIN + usable * (meters.x / span.toFloat()) + usable * ((span - maxX) / span / 2).toFloat()
            val y = MARGIN + usable * (1f - meters.y / span.toFloat()) - usable * ((span - maxY) / span / 2).toFloat()
            return Offset(x, y)
        }
    }

    private companion object {
        const val DEFAULT_DIVERGENCE_METERS = 40.0
        const val METERS_PER_DEGREE = 111_195.0
        const val MIN_SPAN_METERS = 50.0
        const val MARGIN = 0.15f
    }
}
