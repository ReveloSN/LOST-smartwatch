package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

private const val LARGE_TEXT_FACTOR = 1.3f

/** Modo texto grande (lámina AX.1): aumenta un 30 % la escala de fuente sobre la del sistema. */
@Composable
fun LargeTextScale(isEnabled: Boolean, content: @Composable () -> Unit) {
    if (!isEnabled) return content()
    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, density.fontScale * LARGE_TEXT_FACTOR),
        content = content
    )
}
