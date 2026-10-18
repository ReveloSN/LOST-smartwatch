package com.lost.domain.model

/** Un punto GPS con el instante en que se tomó (milisegundos desde época). */
data class GeoPoint(val latitude: Double, val longitude: Double, val timestampMillis: Long = 0L)
