package com.lost.watch.feature.newplace

import com.lost.domain.model.PlaceKind

/** H.1 · Destino marcado en un lugar sin rutina. */
data class NewPlaceUiState(val destinationName: String, val kind: PlaceKind, val isStarted: Boolean)
