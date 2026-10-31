package com.lost.watch.ui.map

import android.content.Context
import android.graphics.BitmapFactory
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.lost.designsystem.components.organisms.MapTileImage
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.domain.error.LostResult
import com.lost.domain.map.LruCache
import com.lost.domain.map.MapViewport
import com.lost.domain.map.RouteMap
import com.lost.domain.map.TileKey
import com.lost.domain.map.ViewportFrame
import com.lost.domain.model.GeoPoint
import com.lost.domain.usecase.LoadRouteMapUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Prepara el mapa real de una pantalla: encuadra las rutas, trae las teselas (caché → red),
 * las decodifica una sola vez (LRU de imágenes) y proyecta las rutas a fracciones del mapa.
 */
@Singleton
class RouteMapPresenter @Inject constructor(
    @param:ApplicationContext private val context: Context,
    private val loadRouteMap: LoadRouteMapUseCase
) {
    private val images = LruCache<TileKey, ImageBitmap>(DECODED_TILES)

    suspend fun present(routes: MapRoutes): LostResult<RouteMapPicture> {
        val screenPx = context.resources.displayMetrics.widthPixels
        val viewportPx = (screenPx / ComponentTokens.RealMap.TILE_DISPLAY_SCALE).toInt()
        val frame = ViewportFrame(viewportPx, viewportPx, routes.marginFraction)
        return when (val result = loadRouteMap(routes.habitual + routes.today, frame)) {
            is LostResult.Failure -> result
            is LostResult.Success -> LostResult.Success(picture(result.value, routes.habitual, routes.today))
        }
    }

    private suspend fun picture(map: RouteMap, habitual: List<GeoPoint>, today: List<GeoPoint>) =
        withContext(Dispatchers.Default) {
            RouteMapPicture(
                tiles = tileImages(map),
                habitual = project(map.viewport, habitual),
                today = project(map.viewport, today)
            )
        }

    private fun tileImages(map: RouteMap): List<MapTileImage> {
        val loaded = map.tiles.associateBy { it.key }
        return map.viewport.visibleTiles().mapNotNull { placement ->
            val image = images[placement.key] ?: loaded[placement.key]?.let { decode(it.png) }
                ?.also { images[placement.key] = it }
            image?.let { MapTileImage(it, placement.left, placement.top, placement.width, placement.height) }
        }
    }

    private fun decode(png: ByteArray): ImageBitmap? = BitmapFactory.decodeByteArray(png, 0, png.size)?.asImageBitmap()

    private fun project(viewport: MapViewport, points: List<GeoPoint>): List<Offset> =
        points.map { viewport.fractionOf(it) }.map { Offset(it.x, it.y) }

    private companion object {
        const val DECODED_TILES = 24
    }
}
