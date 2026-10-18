package com.lost.domain.assistance.policy

import com.lost.domain.detection.AssistanceLevel

/** Último eslabón: nivel alto o 30 s sin respuesta, y hay un contacto autorizado → compartir. */
class ShareLocationLink : AssistanceResponseLink {
    override fun respond(context: AssistanceContext): AssistanceAction? {
        val isUrgent = context.result.level == AssistanceLevel.HIGH || context.hasResponseTimedOut
        return if (isUrgent && context.hasAuthorizedContact) AssistanceAction.SHARE_LOCATION else null
    }
}
