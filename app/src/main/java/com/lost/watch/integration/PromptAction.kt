package com.lost.watch.integration

import android.content.Intent
import androidx.annotation.StringRes
import com.lost.domain.assistance.AssistanceEvent
import com.lost.watch.R

/**
 * Una respuesta que se puede dar desde la notificación sin abrir LOST. "Guíame" sí abre la app ([opensApp]):
 * la guía es una flecha que hay que ver.
 */
enum class PromptAction(@param:StringRes val label: Int, val event: AssistanceEvent, val opensApp: Boolean) {
    GUIDE(R.string.assist_guide_me, AssistanceEvent.UserRequestedGuidance, opensApp = true),
    IM_FINE(R.string.assist_im_fine, AssistanceEvent.UserIsFine, opensApp = false),
    GOING_ELSEWHERE(R.string.assist_going_elsewhere, AssistanceEvent.UserIsGoingElsewhere, opensApp = false),
    CANCEL_SHARING(R.string.assist_cancel, AssistanceEvent.CountdownCancelled, opensApp = false);

    companion object {
        const val EXTRA = "com.lost.watch.PROMPT_ACTION"

        fun from(intent: Intent?): PromptAction? = intent?.getStringExtra(EXTRA)?.let { name ->
            entries.firstOrNull { it.name == name }
        }
    }
}
