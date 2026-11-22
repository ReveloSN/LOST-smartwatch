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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Descarga una tesela de tile.openstreetmap.org. Solo viaja la zona (z/x/y), nunca la ubicación exacta.
 * La lectura HTTP es bloqueante y puede quedarse colgada (los timeouts de conexión no cubren todas las
 * etapas TLS), así que corre en su propio trabajo: si pasa el tope, la pantalla sigue con el mapa simple
 * en vez de esperar para siempre. El hilo colgado termina solo cuando el socket se rinde.
 */
class NetworkTileSource @Inject constructor(
    private val http: HttpGetClient,
    @param:IoDispatcher io: CoroutineDispatcher
) : MapTileSource {
    private val downloads = CoroutineScope(SupervisorJob() + io)

    override suspend fun tile(key: TileKey): LostResult<MapTile> {
        val url = OsmUsagePolicy.TILE_URL.format(key.zoom, key.x, key.y)
        val download = downloads.async { http.get(url) }
        return try {
            val bytes = withTimeoutOrNull(OsmUsagePolicy.TILE_TOTAL_TIMEOUT_MILLIS) { download.await() }
            if (bytes == null) LostResult.Failure(LostError.MapUnavailable) else LostResult.Success(MapTile(key, bytes))
        } catch (_: IOException) {
            LostResult.Failure(LostError.MapUnavailable)
        }
    }
}
