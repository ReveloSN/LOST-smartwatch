package com.lost.watch.feature.newplace

/** H.2 / 02.3 · Navegación activa al destino; al llegar, 02.4. */
data class NewPlaceGuideUiState(
    val destinationName: String?,
    val distanceMeters: Double?,
    val bearingDegrees: Float,
    val hasArrived: Boolean,
    /** Giro de la flecha en pantalla: con brújula, relativo a hacia dónde mira el usuario. */
    val arrowDegrees: Float = bearingDegrees
)
