package com.lost.watch.feature.newplace

import com.lost.domain.model.PlaceKind

/**
 * H.1 · Destino marcado en un lugar sin rutina.
 * @property sendsWalkingRoute con el mapa real encendido se pide una ruta a pie: la pantalla lo dice.
 */
data class NewPlaceUiState(
    val destinationName: String,
    val kind: PlaceKind,
    val isStarted: Boolean,
    val sendsWalkingRoute: Boolean = false
)
