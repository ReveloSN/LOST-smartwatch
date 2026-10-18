package com.lost.watch.feature.insights

/** Acciones en Tu semana. */
sealed interface InsightsUiEvent {
    data object Refresh : InsightsUiEvent
}
