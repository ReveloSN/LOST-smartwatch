package com.lost.watch.feature.assistance

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.AssistanceTimings
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.error.LostError
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.ui.LostErrorToUiMessageMapper
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.Tone
import com.lost.watch.ui.ToneResolver
import javax.inject.Inject

/** Traduce el estado de la máquina de asistencia a lo que muestra la capa de alerta. */
class AssistanceUiStateMapper @Inject constructor(
    private val errorMapper: LostErrorToUiMessageMapper,
    private val toneResolver: ToneResolver
) {
    fun map(snapshot: TripSessionSnapshot, contact: AuthorizedContact?, shareError: LostError?): AssistanceUiState =
        when (val state = snapshot.assistance) {
            AssistanceState.Calm -> AssistanceUiState.Hidden

            is AssistanceState.SoftAsk -> AssistanceUiState.SoftAsk(state.reasons)

            is AssistanceState.AskForHelp ->
                AssistanceUiState.AskForHelp(
                    StatusPresentation.of(snapshot.detection.level).atLeastDeviation(),
                    toneResolver.resolve(snapshot.detection.level).atLeastAttentive(),
                    state.reasons,
                    isNewPlace = snapshot.detection.mode == DetectionModeKind.NEW_PLACE
                )

            AssistanceState.Guiding -> AssistanceUiState.Guiding(
                distanceMeters = snapshot.guidance?.distance?.value,
                bearingDegrees = snapshot.guidance?.bearing?.value?.toFloat() ?: 0f,
                destinationName = snapshot.destination?.name?.takeIf { snapshot.routine == null }
            )

            is AssistanceState.SharingCountdown -> AssistanceUiState.SharingCountdown(
                state.secondsLeft,
                AssistanceTimings.SHARING_COUNTDOWN_SECONDS,
                contact?.name
            )

            AssistanceState.LocationShared -> AssistanceUiState.LocationShared(
                contact?.name,
                shareError?.let(errorMapper::map)
            )

            is AssistanceState.Acknowledged -> AssistanceUiState.Acknowledged(state.feedback)
        }

    private fun Tone.atLeastAttentive() = if (this == Tone.RELAXED) Tone.ATTENTIVE else this

    private fun StatusPresentation.atLeastDeviation() = if (this ==
        StatusPresentation.ON_ROUTE
    ) {
        StatusPresentation.DEVIATION
    } else {
        this
    }
}
