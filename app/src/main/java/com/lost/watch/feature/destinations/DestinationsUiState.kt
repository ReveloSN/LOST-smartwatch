package com.lost.watch.feature.destinations

/** Destinos: lista, último texto dictado que no se encontró y destino elegido por voz. */
data class DestinationsUiState(
    val items: List<DestinationItem>,
    val notFoundQuery: String?,
    val matchedDestinationId: String?
)
