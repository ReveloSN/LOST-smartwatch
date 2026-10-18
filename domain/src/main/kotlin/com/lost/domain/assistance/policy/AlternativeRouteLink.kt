package com.lost.domain.assistance.policy

/** Segundo eslabón: si el usuario pidió ayuda, mostrar la ruta de vuelta. */
class AlternativeRouteLink : AssistanceResponseLink {
    override fun respond(context: AssistanceContext): AssistanceAction? =
        if (context.hasUserRequestedGuidance) AssistanceAction.SHOW_ALTERNATIVE_ROUTE else null
}
