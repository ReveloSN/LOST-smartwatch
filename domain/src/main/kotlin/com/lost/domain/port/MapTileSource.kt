package com.lost.domain.port

import com.lost.domain.error.LostResult
import com.lost.domain.map.MapTile
import com.lost.domain.map.TileKey

/** Entrega teselas del mapa. Solo se piden las zonas que se ven; nunca se envía la ubicación del usuario. */
fun interface MapTileSource {
    suspend fun tile(key: TileKey): LostResult<MapTile>
}
