package com.lost.watch.feature.home

/** Acciones del usuario en Inicio. */
sealed interface HomeUiEvent {
    data object StartTrip : HomeUiEvent

    data object StopTrip : HomeUiEvent
}
