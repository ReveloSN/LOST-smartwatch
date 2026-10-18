package com.lost.domain.assistance.policy

import com.lost.domain.detection.AssistanceLevel

/**
 * Primer eslabón: vibrar y preguntar. Nivel medio → "¿Necesitas ayuda?";
 * desvío voluntario → pregunta suave. Alto sin contacto también pregunta (no hay a quién avisar).
 */
class VibrateAndAskLink : AssistanceResponseLink {
    override fun respond(context: AssistanceContext): AssistanceAction? {
        if (context.hasUserRequestedGuidance || context.hasResponseTimedOut) return null
        val result = context.result
        return when {
            result.level == AssistanceLevel.MEDIUM -> AssistanceAction.VIBRATE_AND_ASK
            result.level == AssistanceLevel.HIGH && !context.hasAuthorizedContact -> AssistanceAction.VIBRATE_AND_ASK
            result.level == AssistanceLevel.LOW && result.isVoluntaryDetour -> AssistanceAction.SOFT_ASK
            else -> null
        }
    }
}
