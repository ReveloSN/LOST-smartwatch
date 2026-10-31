package com.lost.data.map

import com.lost.domain.error.LostResult
import com.lost.domain.map.LruCache
import com.lost.domain.map.MapTile
import com.lost.domain.map.TileKey
import com.lost.domain.port.MapTileSource
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Busca la tesela primero en memoria (LRU), luego en disco y solo al final en la red.
 * Lo descargado se guarda en ambas cachés: la misma zona no vuelve a pedirse al servidor.
 */
class CachedMapTileSource(
    private val memory: LruCache<TileKey, MapTile>,
    private val disk: TileDiskCache,
    private val network: MapTileSource,
    private val io: CoroutineDispatcher
) : MapTileSource {
    override suspend fun tile(key: TileKey): LostResult<MapTile> {
        memory[key]?.let { return LostResult.Success(it) }
        val stored = withContext(io) { disk.read(key) }
        if (stored != null) return LostResult.Success(MapTile(key, stored).also { memory[key] = it })
        val downloaded = network.tile(key)
        downloaded.valueOrNull()?.let { tile ->
            memory[key] = tile
            withContext(io) { disk.write(key, tile.png) }
        }
        return downloaded
    }
}
