package com.lost.watch.feature.destinations

import com.lost.domain.model.PlaceSuggestion
import com.lost.watch.ui.UiMessage

/**
 * Resultado de dictar un destino: uno guardado que coincide (se abre solo), lugares reales
 * encontrados en OpenStreetMap, nada encontrado, o un aviso de tres partes si no hubo conexión.
 */
data class DestinationSearchState(
    val matchedId: String? = null,
    val notFoundQuery: String? = null,
    val results: List<PlaceSuggestion> = emptyList(),
    val isSearching: Boolean = false,
    val error: UiMessage? = null
)
