package com.lost.domain.model

/**
 * Un punto GPS con el instante en que se tomó (milisegundos desde época) y, si la fuente la conoce,
 * su precisión: el radio en metros dentro del cual está la persona con un 68 % de confianza
 * (`Location.accuracy`). Es `null` cuando no se sabe, por ejemplo en los GPX de la demo.
 */
data class GeoPoint(
    val latitude: Double,
    val longitude: Double,
    val timestampMillis: Long = 0L,
    val accuracyMeters: Double? = null
)
