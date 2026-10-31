package com.lost.watch.feature.monitoring

/** Acciones en el monitoreo. */
sealed interface MonitoringUiEvent {
    /** Tocar el estado: "¿Por qué te pregunto?". */
    data object OpenExplanation : MonitoringUiEvent
}
