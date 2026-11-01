package com.lost.watch.feature.places

/** Acciones en Lugares donde más te desorientas. */
sealed interface PlacesUiEvent {
    data object Refresh : PlacesUiEvent
}
