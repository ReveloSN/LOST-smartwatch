package com.lost.domain.assistance.policy

/**
 * Eslabón de la cadena de responsabilidad. Si no le corresponde actuar devuelve `null`
 * y la cadena pasa al siguiente.
 */
fun interface AssistanceResponseLink {
    fun respond(context: AssistanceContext): AssistanceAction?
}
