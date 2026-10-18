package com.lost.designsystem.components.molecules

import com.lost.designsystem.tokens.StatusTone

/** Lo que muestra el indicador: distancia ya formateada, detalle y rumbo relativo de la flecha. */
data class DistanceContent(
    val distance: String,
    val detail: String,
    val arrowRotationDegrees: Float,
    val tone: StatusTone = StatusTone.ON_ROUTE
)
