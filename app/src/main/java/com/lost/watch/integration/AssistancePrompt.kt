package com.lost.watch.integration

import androidx.annotation.StringRes
import com.lost.watch.R

/** 05.3 · Pregunta que LOST hace por notificación cuando la app no está a la vista, con sus dos respuestas. */
enum class AssistancePrompt(
    @param:StringRes val question: Int,
    val primary: PromptAction,
    val secondary: PromptAction
) {
    ASK_FOR_HELP(R.string.notification_ask_question, PromptAction.GUIDE, PromptAction.IM_FINE),
    SOFT_ASK(R.string.assist_soft_ask_title, PromptAction.GOING_ELSEWHERE, PromptAction.GUIDE)
}
