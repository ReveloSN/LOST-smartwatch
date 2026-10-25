package com.lost.watch.feature.errors

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.watch.ui.UiMessage

/** Un [UiMessage] ya convertido a texto en el idioma del usuario. */
data class UiMessageText(val whatHappened: String, val why: String?, val whatToDo: String) {
    /** Las tres partes en una sola frase, para avisos dentro de una lista. */
    fun joined(): String = listOfNotNull(whatHappened, why, whatToDo).joinToString(" ")
}

@Composable
fun uiMessageText(message: UiMessage): UiMessageText = UiMessageText(
    whatHappened = message.whatHappenedArgument?.let { stringResource(message.whatHappened, it) }
        ?: stringResource(message.whatHappened),
    why = message.why?.let { stringResource(it) },
    whatToDo = stringResource(message.whatToDo)
)
