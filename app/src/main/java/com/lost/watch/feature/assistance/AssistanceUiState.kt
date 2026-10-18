package com.lost.watch.feature.assistance

import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.DeviationReason
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.Tone
import com.lost.watch.ui.UiMessage

/** Lo que muestra la capa de alerta. [Hidden] = no hay alerta y se ve la pantalla de abajo. */
sealed interface AssistanceUiState {
    data object Hidden : AssistanceUiState

    data class SoftAsk(val reasons: List<DeviationReason>) : AssistanceUiState

    data class AskForHelp(
        val status: StatusPresentation,
        val tone: Tone,
        val reasons: List<DeviationReason>,
        val isNewPlace: Boolean = false
    ) : AssistanceUiState

    data class Guiding(val distanceMeters: Double?, val bearingDegrees: Float, val destinationName: String?) :
        AssistanceUiState

    data class SharingCountdown(val secondsLeft: Int, val totalSeconds: Int, val contactName: String?) :
        AssistanceUiState

    data class LocationShared(val contactName: String?, val error: UiMessage?) : AssistanceUiState

    data class Acknowledged(val feedback: UserFeedback) : AssistanceUiState

    data object BackOnRoute : AssistanceUiState
}
