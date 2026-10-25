package com.lost.watch.ui.map

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.lost.designsystem.components.organisms.MapTileImage

/** Teselas y rutas ya proyectadas: listas para [com.lost.designsystem.components.organisms.RealMapView]. */
@Immutable
data class RouteMapPicture(val tiles: List<MapTileImage>, val habitual: List<Offset>, val today: List<Offset>)
