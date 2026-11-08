package com.lost.data.map

import com.lost.data.di.IoDispatcher
import com.lost.data.online.HttpGetClient
import com.lost.data.online.OsmUsagePolicy
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.map.MapTile
import com.lost.domain.map.TileKey
import com.lost.domain.port.MapTileSource
import java.io.IOException
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/** Descarga una tesela de tile.openstreetmap.org. Solo viaja la zona (z/x/y), nunca la ubicación exacta. */
class NetworkTileSource @Inject constructor(
    private val http: HttpGetClient,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : MapTileSource {
    override suspend fun tile(key: TileKey): LostResult<MapTile> = withContext(io) {
        try {
            val url = OsmUsagePolicy.TILE_URL.format(key.zoom, key.x, key.y)
            LostResult.Success(MapTile(key, http.get(url)))
        } catch (_: IOException) {
            LostResult.Failure(LostError.MapUnavailable)
        }
    }
}
