package com.lost.watch.feature.destinations

/** Acciones en Destinos. */
sealed interface DestinationsUiEvent {
    /** Lo que el usuario dictó ("Llévame a la biblioteca"). */
    data class VoiceQuery(val spokenText: String) : DestinationsUiEvent

    /** La pantalla ya navegó al destino encontrado. */
    data object MatchConsumed : DestinationsUiEvent
}
