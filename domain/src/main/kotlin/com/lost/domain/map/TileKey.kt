package com.lost.domain.map

/** Una tesela del mapa: nivel de zoom y posición (x hacia el este, y hacia el sur). */
data class TileKey(val zoom: Int, val x: Int, val y: Int)
