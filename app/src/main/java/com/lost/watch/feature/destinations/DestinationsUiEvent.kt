package com.lost.watch.feature.destinations

import com.lost.domain.model.PlaceSuggestion

/** Acciones en Destinos. */
sealed interface DestinationsUiEvent {
    /** Lo que el usuario dictó ("Llévame a la biblioteca"). */
    data class VoiceQuery(val spokenText: String) : DestinationsUiEvent

    /** Eligió un lugar real encontrado en OpenStreetMap: se guarda con sus coordenadas. */
    data class ChoosePlace(val place: PlaceSuggestion) : DestinationsUiEvent

    /** La pantalla ya navegó al destino encontrado. */
    data object MatchConsumed : DestinationsUiEvent
}
