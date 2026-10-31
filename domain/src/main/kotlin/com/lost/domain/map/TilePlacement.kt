package com.lost.domain.map

/** Dónde se dibuja una tesela: esquina superior izquierda y lado, en fracciones del ancho del mapa. */
data class TilePlacement(val key: TileKey, val left: Float, val top: Float, val width: Float, val height: Float)
