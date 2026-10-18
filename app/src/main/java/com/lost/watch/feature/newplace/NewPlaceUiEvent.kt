package com.lost.watch.feature.newplace

/** Acciones del modo lugar nuevo. */
sealed interface NewPlaceUiEvent {
    data object Start : NewPlaceUiEvent

    data object SaveDestination : NewPlaceUiEvent

    data object Finish : NewPlaceUiEvent
}
