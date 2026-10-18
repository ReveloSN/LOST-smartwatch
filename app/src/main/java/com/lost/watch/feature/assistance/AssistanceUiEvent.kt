package com.lost.watch.feature.assistance

/** Respuestas del usuario en la capa de alerta. */
sealed interface AssistanceUiEvent {
    data object GoingElsewhere : AssistanceUiEvent

    data object GuideMe : AssistanceUiEvent

    data object ImFine : AssistanceUiEvent

    data object CancelSharing : AssistanceUiEvent

    data object KeepGuiding : AssistanceUiEvent

    data object DismissBackOnRoute : AssistanceUiEvent
}
