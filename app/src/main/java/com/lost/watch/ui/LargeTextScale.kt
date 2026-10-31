package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private const val LARGE_TEXT_FACTOR = 1.3f

/**
 * Modo texto grande (lámina AX.1): aumenta un 30 % la escala de fuente sobre la del sistema.
 * Siempre pasa por el mismo `CompositionLocalProvider`: si el contenido cambiara de lugar en el árbol
 * al activarlo, se perdería el estado de la pantalla (la app volvía a Inicio).
 */
@Composable
fun LargeTextScale(isEnabled: Boolean, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val factor = if (isEnabled) LARGE_TEXT_FACTOR else 1f
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, density.fontScale * factor),
        content = content
    )
}
