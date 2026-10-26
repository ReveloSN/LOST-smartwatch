package com.lost.watch.feature.assistance

/** Respuestas del usuario en la capa de alerta. */
sealed interface AssistanceUiEvent {
    data object GoingElsewhere : AssistanceUiEvent

    data object GuideMe : AssistanceUiEvent

    data object ImFine : AssistanceUiEvent

    data object CancelSharing : AssistanceUiEvent

    data object KeepGuiding : AssistanceUiEvent

    /** "Ya sé dónde estoy": salir de la guía sin haber vuelto a la ruta. */
    data object EndGuidance : AssistanceUiEvent

    data object DismissBackOnRoute : AssistanceUiEvent
}
