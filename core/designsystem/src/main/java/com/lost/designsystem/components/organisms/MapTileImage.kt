package com.lost.designsystem.components.organisms

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.ImageBitmap

/** Una tesela decodificada y su lugar en el mapa (fracciones del ancho y alto). */
@Immutable
data class MapTileImage(val image: ImageBitmap, val left: Float, val top: Float, val width: Float, val height: Float)
