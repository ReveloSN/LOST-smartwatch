package com.lost.domain.usecase

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.map.RouteMap
import com.lost.domain.map.ViewportFitter
import com.lost.domain.map.ViewportFrame
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.MapTileSource
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Encuadra las rutas y trae solo las teselas visibles (en paralelo). Si no llega ninguna,
 * devuelve [LostError.MapUnavailable] y la pantalla vuelve al mapa simple.
 */
class LoadRouteMapUseCase @Inject constructor(private val tileSource: MapTileSource) {
    private val fitter = ViewportFitter()

    suspend operator fun invoke(points: List<GeoPoint>, frame: ViewportFrame): LostResult<RouteMap> = coroutineScope {
        val viewport = fitter.fit(points, frame) ?: return@coroutineScope LostResult.Failure(LostError.MapUnavailable)
        val tiles = viewport.visibleTiles()
            .map { placement -> async { tileSource.tile(placement.key).valueOrNull() } }
            .awaitAll()
            .filterNotNull()
        if (tiles.isEmpty()) {
            LostResult.Failure(
                LostError.MapUnavailable
            )
        } else {
            LostResult.Success(RouteMap(viewport, tiles))
        }
    }
}
