package com.lost.domain.assistance

import com.lost.domain.assistance.AssistanceEvent.AcknowledgementElapsed
import com.lost.domain.assistance.AssistanceEvent.CountdownCancelled
import com.lost.domain.assistance.AssistanceEvent.CountdownTicked
import com.lost.domain.assistance.AssistanceEvent.DestinationReached
import com.lost.domain.assistance.AssistanceEvent.DetectionUpdated
import com.lost.domain.assistance.AssistanceEvent.ResponseTimedOut
import com.lost.domain.assistance.AssistanceEvent.RouteRecovered
import com.lost.domain.assistance.AssistanceEvent.UserIsFine
import com.lost.domain.assistance.AssistanceEvent.UserIsGoingElsewhere
import com.lost.domain.assistance.AssistanceEvent.UserRequestedGuidance
import com.lost.domain.assistance.AssistanceState.Acknowledged
import com.lost.domain.assistance.AssistanceState.AskForHelp
import com.lost.domain.assistance.AssistanceState.Calm
import com.lost.domain.assistance.AssistanceState.Guiding
import com.lost.domain.assistance.AssistanceState.LocationShared
import com.lost.domain.assistance.AssistanceState.SharingCountdown
import com.lost.domain.assistance.AssistanceState.SoftAsk
import com.lost.domain.assistance.policy.AssistanceAction
import com.lost.domain.assistance.policy.AssistanceContext
import com.lost.domain.assistance.policy.AssistanceResponsePolicy
import com.lost.domain.detection.DetectionResult

/**
 * Máquina de estados de la escalada de ayuda (sección 8.4 del plan).
 * Es pura: recibe el estado y un evento y devuelve el siguiente estado, sin efectos.
 * Vibrar, guardar métricas o aprender lo hace quien la usa, al observar las transiciones.
 */
class AssistanceStateMachine(private val policy: AssistanceResponsePolicy = AssistanceResponsePolicy.standard()) {

    fun next(state: AssistanceState, event: AssistanceEvent): AssistanceState {
        if (event == DestinationReached) return Calm
        return when (state) {
            Calm -> fromCalm(event)
            is SoftAsk -> fromSoftAsk(state, event)
            is AskForHelp -> fromAskForHelp(state, event)
            Guiding -> fromGuiding(event)
            is SharingCountdown -> fromCountdown(state, event)
            LocationShared -> fromLocationShared(event)
            is Acknowledged -> if (event == AcknowledgementElapsed) Calm else state
        }
    }

    private fun fromCalm(event: AssistanceEvent): AssistanceState =
        if (event is DetectionUpdated) escalate(event.result, event.hasAuthorizedContact, Calm) else Calm

    private fun fromSoftAsk(state: SoftAsk, event: AssistanceEvent): AssistanceState = when (event) {
        UserIsGoingElsewhere -> Acknowledged(UserFeedback.GOING_ELSEWHERE)
        UserRequestedGuidance -> Guiding
        is ResponseTimedOut -> Calm
        is DetectionUpdated -> escalate(event.result, event.hasAuthorizedContact, state)
        else -> state
    }

    private fun fromAskForHelp(state: AskForHelp, event: AssistanceEvent): AssistanceState = when (event) {
        UserRequestedGuidance -> Guiding
        UserIsFine -> Acknowledged(UserFeedback.IM_FINE)
        is ResponseTimedOut -> decide(state.reasonsContext(event.hasAuthorizedContact), state)
        is DetectionUpdated -> escalate(event.result, event.hasAuthorizedContact, state).keepAskingIfCalmer(state)
        else -> state
    }

    private fun fromGuiding(event: AssistanceEvent): AssistanceState = if (event == RouteRecovered) Calm else Guiding

    private fun fromCountdown(state: SharingCountdown, event: AssistanceEvent): AssistanceState = when (event) {
        CountdownTicked -> if (state.secondsLeft <= 1) LocationShared else SharingCountdown(state.secondsLeft - 1)
        CountdownCancelled -> Calm
        else -> state
    }

    private fun fromLocationShared(event: AssistanceEvent): AssistanceState = when (event) {
        UserRequestedGuidance -> Guiding
        RouteRecovered -> Calm
        else -> LocationShared
    }

    private fun escalate(result: DetectionResult, hasContact: Boolean, current: AssistanceState): AssistanceState {
        val context = AssistanceContext(result, false, false, hasContact)
        return decide(context, current)
    }

    private fun decide(context: AssistanceContext, current: AssistanceState): AssistanceState =
        when (policy.decide(context)) {
            AssistanceAction.NONE -> current
            AssistanceAction.SOFT_ASK -> SoftAsk(context.result.reasons)
            AssistanceAction.VIBRATE_AND_ASK -> AskForHelp(context.result.reasons)
            AssistanceAction.SHOW_ALTERNATIVE_ROUTE -> Guiding
            AssistanceAction.SHARE_LOCATION -> SharingCountdown(AssistanceTimings.SHARING_COUNTDOWN_SECONDS)
        }

    /** Una vez que se preguntó, una lectura más tranquila no borra la pregunta: espera la respuesta. */
    private fun AssistanceState.keepAskingIfCalmer(asking: AskForHelp): AssistanceState =
        if (this is Calm || this is SoftAsk) asking else this

    private fun AskForHelp.reasonsContext(hasContact: Boolean) = AssistanceContext(
        result = DetectionResult.CALM.copy(reasons = reasons),
        hasUserRequestedGuidance = false,
        hasResponseTimedOut = true,
        hasAuthorizedContact = hasContact
    )
}
