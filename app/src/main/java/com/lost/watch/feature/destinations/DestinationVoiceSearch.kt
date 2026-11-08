package com.lost.watch.feature.destinations

import com.lost.domain.error.LostResult
import com.lost.domain.model.Destination
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.PlaceSuggestion
import com.lost.domain.port.IdGenerator
import com.lost.domain.usecase.ObserveSettingsUseCase
import com.lost.domain.usecase.SaveDestinationUseCase
import com.lost.domain.usecase.SearchPlacesUseCase
import com.lost.watch.ui.LostErrorToUiMessageMapper
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Busca lo dictado: primero entre los destinos guardados (sin red); si no está y el mapa real
 * está encendido, en OpenStreetMap enviando solo el texto. Elegir un resultado lo guarda con sus coordenadas.
 */
class DestinationVoiceSearch @Inject constructor(
    private val matcher: DestinationMatcher,
    private val observeSettings: ObserveSettingsUseCase,
    private val searchPlaces: SearchPlacesUseCase,
    private val saveDestination: SaveDestinationUseCase,
    private val ids: IdGenerator,
    private val errors: LostErrorToUiMessageMapper
) {
    fun matchSaved(spokenText: String, saved: List<DestinationItem>): DestinationSearchState? =
        matcher.match(spokenText, saved)?.let { DestinationSearchState(matchedId = it.id) }

    suspend fun searchOnline(spokenText: String): DestinationSearchState {
        if (!observeSettings().first().isRealMapEnabled) return DestinationSearchState(notFoundQuery = spokenText)
        return when (val result = searchPlaces(SpokenPlaceQuery.clean(spokenText))) {
            is LostResult.Success -> if (result.value.isEmpty()) {
                DestinationSearchState(notFoundQuery = spokenText)
            } else {
                DestinationSearchState(results = result.value)
            }

            is LostResult.Failure -> DestinationSearchState(error = errors.map(result.error))
        }
    }

    suspend fun choose(place: PlaceSuggestion): DestinationSearchState {
        val destination = Destination(ids.newId(), place.name, place.location, PlaceKind.OTHER, lastUsedAtMillis = null)
        saveDestination(destination)
        return DestinationSearchState(matchedId = destination.id)
    }
}
