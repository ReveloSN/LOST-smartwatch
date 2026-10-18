package com.lost.watch.feature.monitoring

/** Acciones en el monitoreo. */
sealed interface MonitoringUiEvent {
    /** "Toca para saber por qué". */
    data object OpenExplanation : MonitoringUiEvent
}
