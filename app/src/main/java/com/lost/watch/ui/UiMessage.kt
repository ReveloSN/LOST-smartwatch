package com.lost.watch.ui

import androidx.annotation.StringRes

/**
 * Mensaje de error de tres partes, obligatorio por estructura:
 * qué pasó · por qué (solo si ayuda a actuar) · qué hacer ahora. Nunca códigos internos.
 */
data class UiMessage(
    @param:StringRes val whatHappened: Int,
    @param:StringRes val why: Int?,
    @param:StringRes val whatToDo: Int,
    val whatHappenedArgument: Int? = null
)
