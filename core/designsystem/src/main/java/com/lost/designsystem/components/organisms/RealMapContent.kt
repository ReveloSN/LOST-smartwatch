package com.lost.designsystem.components.organisms

import androidx.compose.runtime.Immutable
import androidx.compose.ui.geometry.Offset
import com.lost.designsystem.tokens.StatusTone

/**
 * Lo que dibuja [RealMapView]: teselas ya decodificadas y las dos rutas, todo en fracciones
 * del tamaño del mapa (0..1). El design system no sabe de red ni de coordenadas geográficas.
 */
@Immutable
data class RealMapContent(
    val tiles: List<MapTileImage>,
    val habitual: List<Offset>,
    val today: List<Offset>,
    val tone: StatusTone,
    val description: String,
    val attribution: String?
)
