package com.lost.domain.model

/** Un lugar real encontrado al buscar un destino (nombre corto y coordenadas). */
data class PlaceSuggestion(val name: String, val detail: String, val location: GeoPoint)
