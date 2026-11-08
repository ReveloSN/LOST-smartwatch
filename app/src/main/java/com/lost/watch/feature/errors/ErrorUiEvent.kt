package com.lost.watch.feature.errors

/** Acciones sobre los mensajes de error. */
sealed interface ErrorUiEvent {
    /** Volver a revisar el estado del reloj (al volver a la app o tras responder al permiso). */
    data object Recheck : ErrorUiEvent

    /** "Entendido" en un aviso: no vuelve a tapar Inicio mientras la app siga abierta. */
    data object Acknowledge : ErrorUiEvent
}
