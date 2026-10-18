package com.lost.designsystem.components.organisms

import androidx.compose.ui.geometry.Offset
import com.lost.designsystem.tokens.StatusTone

/**
 * Datos del mini-mapa de doble línea. Los puntos van normalizados entre 0 y 1
 * (la app proyecta las coordenadas GPS antes de pasarlas).
 */
data class RouteComparisonContent(
    val habitual: List<Offset>,
    val today: List<Offset>,
    val divergence: Offset?,
    val tone: StatusTone,
    val description: String
)
