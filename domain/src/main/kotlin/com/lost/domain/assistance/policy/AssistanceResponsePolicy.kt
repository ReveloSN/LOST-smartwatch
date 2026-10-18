package com.lost.domain.assistance.policy

/**
 * Cadena de responsabilidad: vibrar → ruta alternativa → compartir ubicación.
 * Recorre los eslabones en orden y devuelve la acción del primero que decide actuar.
 */
class AssistanceResponsePolicy(private val links: List<AssistanceResponseLink>) {
    fun decide(context: AssistanceContext): AssistanceAction =
        links.firstNotNullOfOrNull { it.respond(context) } ?: AssistanceAction.NONE

    companion object {
        fun standard() = AssistanceResponsePolicy(
            listOf(VibrateAndAskLink(), AlternativeRouteLink(), ShareLocationLink())
        )
    }
}
