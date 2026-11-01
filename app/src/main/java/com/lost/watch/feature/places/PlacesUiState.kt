package com.lost.watch.feature.places

import com.lost.domain.metrics.DisorientationPlace

/** 03.2 · Lugares donde más te desorientas. Mientras carga no se muestra el estado vacío. */
data class PlacesUiState(val isLoading: Boolean, val places: List<DisorientationPlace>) {
    val isEmpty: Boolean get() = !isLoading && places.isEmpty()

    companion object {
        val LOADING = PlacesUiState(isLoading = true, places = emptyList())
    }
}
