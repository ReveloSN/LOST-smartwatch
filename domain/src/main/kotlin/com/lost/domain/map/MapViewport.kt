package com.lost.domain.map

import com.lost.domain.model.GeoPoint

/**
 * Lo que se ve del mapa: un nivel de zoom, el centro en píxeles del mundo y el tamaño
 * de la ventana en píxeles de tesela. Sabe qué teselas hacen falta y dónde cae cada punto.
 */
data class MapViewport(val zoom: Int, val centerX: Double, val centerY: Double, val widthPx: Int, val heightPx: Int) {
    private val left: Double get() = centerX - widthPx / 2.0
    private val top: Double get() = centerY - heightPx / 2.0

    /** Teselas visibles (normalmente 4 a 9 en un reloj): nunca se piden más de las que se ven. */
    fun visibleTiles(): List<TilePlacement> {
        val maxIndex = (1 shl zoom) - 1
        val xs = WebMercator.tileIndex(left)..WebMercator.tileIndex(left + widthPx - 1)
        val ys = WebMercator.tileIndex(top)..WebMercator.tileIndex(top + heightPx - 1)
        return ys.filter { it in 0..maxIndex }.flatMap { y ->
            xs.map { x -> placement(TileKey(zoom, Math.floorMod(x, maxIndex + 1), y), x) }
        }
    }

    fun fractionOf(point: GeoPoint): ScreenFraction = ScreenFraction(
        ((WebMercator.worldX(point.longitude, zoom) - left) / widthPx).toFloat(),
        ((WebMercator.worldY(point.latitude, zoom) - top) / heightPx).toFloat()
    )

    private fun placement(key: TileKey, unwrappedX: Int): TilePlacement {
        val size = WebMercator.TILE_SIZE.toDouble()
        return TilePlacement(
            key = key,
            left = ((unwrappedX * size - left) / widthPx).toFloat(),
            top = ((key.y * size - top) / heightPx).toFloat(),
            width = (size / widthPx).toFloat(),
            height = (size / heightPx).toFloat()
        )
    }
}
