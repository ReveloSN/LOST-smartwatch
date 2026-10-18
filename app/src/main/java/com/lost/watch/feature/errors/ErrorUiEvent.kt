package com.lost.watch.feature.errors

/** Acciones sobre los mensajes de error. */
sealed interface ErrorUiEvent {
    /** Volver a revisar el estado del reloj (al volver a la app). */
    data object Recheck : ErrorUiEvent
}
