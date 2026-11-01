package com.lost.watch.integration

/** Dónde se muestra la pregunta fuera de la app (en el reloj, una notificación con dos acciones). */
interface PromptNotifications {
    fun show(prompt: AssistancePrompt)

    fun cancel()
}
