package com.lost.watch.integration

import androidx.annotation.StringRes
import com.lost.watch.R

/**
 * 05.3 · Lo que LOST pregunta por notificación cuando la app no está a la vista, con sus respuestas. La cuenta
 * regresiva solo ofrece "Cancelar": avisar al contacto sin poder evitarlo con la pantalla apagada no sería
 * consentido.
 */
enum class AssistancePrompt(
    @param:StringRes val question: Int,
    val primary: PromptAction,
    val secondary: PromptAction?
) {
    ASK_FOR_HELP(R.string.notification_ask_question, PromptAction.GUIDE, PromptAction.IM_FINE),
    SOFT_ASK(R.string.assist_soft_ask_title, PromptAction.GOING_ELSEWHERE, PromptAction.GUIDE),
    SHARING_SOON(R.string.notification_sharing_soon, PromptAction.CANCEL_SHARING, null)
}
