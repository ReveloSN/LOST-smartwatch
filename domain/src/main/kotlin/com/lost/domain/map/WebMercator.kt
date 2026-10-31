package com.lost.domain.map

import kotlin.math.PI
import kotlin.math.floor
import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.tan

/**
 * Proyección Web Mercator (la de OpenStreetMap): convierte latitud/longitud en píxeles
 * del "mundo" a un nivel de zoom, donde el mundo mide `256 · 2^zoom` píxeles de lado.
 */
object WebMercator {
    const val TILE_SIZE = 256
    const val MAX_LATITUDE = 85.05112878

    fun worldSize(zoom: Int): Double = TILE_SIZE * 2.0.pow(zoom)

    fun worldX(longitude: Double, zoom: Int): Double = (longitude + HALF_TURN) / FULL_TURN * worldSize(zoom)

    fun worldY(latitude: Double, zoom: Int): Double {
        val lat = Math.toRadians(latitude.coerceIn(-MAX_LATITUDE, MAX_LATITUDE))
        val mercator = ln(tan(QUARTER_TURN_RADIANS + lat / HALF))
        return (1 - mercator / PI) / HALF * worldSize(zoom)
    }

    /** Coordenada de la tesela que contiene un píxel del mundo. */
    fun tileIndex(worldPixel: Double): Int = floor(worldPixel / TILE_SIZE).toInt()

    private const val HALF_TURN = 180.0
    private const val FULL_TURN = 360.0
    private const val HALF = 2
    private const val QUARTER_TURN_RADIANS = PI / 4
}
