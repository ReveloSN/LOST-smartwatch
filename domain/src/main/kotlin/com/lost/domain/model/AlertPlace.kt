package com.lost.domain.model

import com.lost.domain.detection.DeviationCause

/** Dónde y por qué LOST preguntó si la persona necesitaba ayuda. Se guarda solo en el reloj. */
data class AlertPlace(
    val location: GeoPoint,
    val timestampMillis: Long,
    val cause: DeviationCause?,
    val routineName: String?
)
