package com.lost.watch.integration

import com.lost.domain.assistance.AssistanceState
import javax.inject.Inject

/**
 * Cuándo preguntar por notificación: solo si LOST está preguntando y la app no está a la vista (pantalla
 * apagada u otra app). Con la app abierta ya se ve la alerta a pantalla completa y la notificación sobraría.
 */
class AssistancePromptPolicy @Inject constructor() {
    fun promptFor(state: AssistanceState, isAppVisible: Boolean): AssistancePrompt? = when {
        isAppVisible -> null
        state is AssistanceState.AskForHelp -> AssistancePrompt.ASK_FOR_HELP
        state is AssistanceState.SoftAsk -> AssistancePrompt.SOFT_ASK
        else -> null
    }
}
