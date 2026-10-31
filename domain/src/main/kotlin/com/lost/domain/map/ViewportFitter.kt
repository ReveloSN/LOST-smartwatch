package com.lost.domain.map

import com.lost.domain.model.GeoPoint

/**
 * Ajusta el encuadre solo: elige el zoom más cercano en el que todos los puntos caben
 * dentro de la ventana dejando un margen, y centra el recuadro. Sin gestos de zoom ni arrastre.
 */
class ViewportFitter(private val minZoom: Int = MIN_ZOOM, private val maxZoom: Int = MAX_ZOOM) {
    fun fit(points: List<GeoPoint>, frame: ViewportFrame): MapViewport? {
        if (points.isEmpty()) return null
        val zoom = (maxZoom downTo minZoom).firstOrNull { fits(points, it, frame) } ?: minZoom
        val xs = points.map { WebMercator.worldX(it.longitude, zoom) }
        val ys = points.map { WebMercator.worldY(it.latitude, zoom) }
        return MapViewport(
            zoom = zoom,
            centerX = (xs.min() + xs.max()) / 2,
            centerY = (ys.min() + ys.max()) / 2,
            widthPx = frame.widthPx,
            heightPx = frame.heightPx
        )
    }

    private fun fits(points: List<GeoPoint>, zoom: Int, frame: ViewportFrame): Boolean {
        val xs = points.map { WebMercator.worldX(it.longitude, zoom) }
        val ys = points.map { WebMercator.worldY(it.latitude, zoom) }
        val usable = 1 - 2 * frame.marginFraction
        return xs.max() - xs.min() <= frame.widthPx * usable && ys.max() - ys.min() <= frame.heightPx * usable
    }

    private companion object {
        const val MIN_ZOOM = 3
        const val MAX_ZOOM = 17
    }
}
