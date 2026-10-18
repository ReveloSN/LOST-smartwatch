package com.lost.watch.feature.destinations

import com.lost.domain.model.PlaceKind

/** Un destino de la lista con su etiqueta ("Sugerido" o "Reciente"). */
data class DestinationItem(
    val id: String,
    val name: String,
    val kind: PlaceKind,
    val isSuggested: Boolean,
    val isRecent: Boolean
)
