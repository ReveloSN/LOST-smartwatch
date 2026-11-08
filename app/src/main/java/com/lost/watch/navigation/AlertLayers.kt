package com.lost.watch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.wear.compose.foundation.hierarchicalFocusGroup

/**
 * Pantalla y capa de alerta superpuestas. Con una alerta encima, la corona es de la alerta y la pantalla
 * de abajo desaparece para TalkBack (si no, el lector podía enfocar filas que no se ven); al cerrarla
 * todo vuelve a la pantalla de abajo.
 */
@Composable
fun AlertLayers(isAlertVisible: Boolean, screen: @Composable () -> Unit, alert: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        val hiddenWhileAlert = if (isAlertVisible) Modifier.clearAndSetSemantics {} else Modifier
        Box(Modifier.fillMaxSize().hierarchicalFocusGroup(active = !isAlertVisible).then(hiddenWhileAlert)) {
            screen()
        }
        Box(Modifier.fillMaxSize().hierarchicalFocusGroup(active = isAlertVisible)) { alert() }
    }
}
