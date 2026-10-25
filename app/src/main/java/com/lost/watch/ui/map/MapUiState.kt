package com.lost.watch.ui.map

import com.lost.watch.ui.UiMessage

/** Qué mapa se ve: el simple (apagado o cargando), el real, o el simple con un aviso de tres partes. */
sealed interface MapUiState {
    data object Simple : MapUiState

    data class Real(val picture: RouteMapPicture) : MapUiState

    data class Unavailable(val message: UiMessage) : MapUiState
}
