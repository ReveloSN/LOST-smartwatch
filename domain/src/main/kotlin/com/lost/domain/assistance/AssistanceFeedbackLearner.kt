package com.lost.domain.assistance

import com.lost.domain.session.AssistanceTransition
import com.lost.domain.usecase.ApplyUserFeedbackUseCase
import javax.inject.Inject

/**
 * La IA aprende de las respuestas: "Estoy bien" / "Voy a otro lado" al confirmarse,
 * y "Sí, guíame" en cuanto el usuario acepta la ayuda.
 */
class AssistanceFeedbackLearner @Inject constructor(private val applyFeedback: ApplyUserFeedbackUseCase) {
    suspend fun onTransition(transition: AssistanceTransition) {
        val to = transition.to
        val isAskingState = transition.from is AssistanceState.AskForHelp || transition.from is AssistanceState.SoftAsk
        when {
            to is AssistanceState.Acknowledged && transition.hasChanged -> applyFeedback(to.feedback)
            to == AssistanceState.Guiding && isAskingState -> applyFeedback(UserFeedback.HELP_CONFIRMED)
        }
    }
}
