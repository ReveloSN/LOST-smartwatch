package com.lost.designsystem.components.organisms

import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import com.lost.designsystem.tokens.ComponentTokens.RealMap

/**
 * Convierte las teselas claras de OpenStreetMap en un mapa oscuro para OLED: invierte la luz,
 * apaga casi todo el color y baja el brillo. Así las rutas (aqua y gris) siguen siendo lo principal.
 */
internal object DarkTileFilter {
    private const val CHANNEL_MAX = 255f

    val colorFilter: ColorFilter by lazy {
        val darken = ColorMatrix().apply {
            setToScale(RealMap.TILE_BRIGHTNESS, RealMap.TILE_BRIGHTNESS, RealMap.TILE_BRIGHTNESS, 1f)
        }
        val mute = ColorMatrix().apply { setToSaturation(RealMap.TILE_SATURATION) }
        darken *= mute
        darken *= invert()
        ColorFilter.colorMatrix(darken)
    }

    private fun invert() = ColorMatrix(
        floatArrayOf(
            -1f, 0f, 0f, 0f, CHANNEL_MAX,
            0f, -1f, 0f, 0f, CHANNEL_MAX,
            0f, 0f, -1f, 0f, CHANNEL_MAX,
            0f, 0f, 0f, 1f, 0f
        )
    )
}
