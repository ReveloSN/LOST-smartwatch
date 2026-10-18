package com.lost.domain.model

/** Un destino al que el usuario quiere llegar (dictado por voz, reciente o sugerido). */
data class Destination(
    val id: String,
    val name: String,
    val location: GeoPoint,
    val kind: PlaceKind,
    val lastUsedAtMillis: Long?
)
