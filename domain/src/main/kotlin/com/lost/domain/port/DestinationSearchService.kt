package com.lost.domain.port

import com.lost.domain.error.LostResult
import com.lost.domain.model.PlaceSuggestion

/** Busca lugares reales por nombre. Solo se envía el texto de la búsqueda. */
fun interface DestinationSearchService {
    suspend fun search(query: String): LostResult<List<PlaceSuggestion>>
}
