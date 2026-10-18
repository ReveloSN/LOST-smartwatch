package com.lost.designsystem.components.molecules

import com.lost.designsystem.tokens.StatusTone

/**
 * Lo que muestra el indicador: distancia ya formateada, detalle y rumbo de la flecha.
 * [arrowDescription] le dice a TalkBack hacia dónde apunta ("Flecha que apunta al noreste").
 */
data class DistanceContent(
    val distance: String,
    val detail: String,
    val arrowRotationDegrees: Float,
    val tone: StatusTone = StatusTone.ON_ROUTE,
    val arrowDescription: String? = null
)
