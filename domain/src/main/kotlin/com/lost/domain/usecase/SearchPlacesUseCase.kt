package com.lost.domain.usecase

import com.lost.domain.error.LostResult
import com.lost.domain.model.PlaceSuggestion
import com.lost.domain.port.DestinationSearchService
import javax.inject.Inject

/** Busca lugares reales con el texto dictado, solo cuando el usuario lo envía (sin autocompletar). */
class SearchPlacesUseCase @Inject constructor(private val service: DestinationSearchService) {
    suspend operator fun invoke(query: String): LostResult<List<PlaceSuggestion>> {
        val text = query.trim()
        if (text.length < MIN_QUERY_LENGTH) return LostResult.Success(emptyList())
        return service.search(text)
    }

    private companion object {
        const val MIN_QUERY_LENGTH = 3
    }
}
