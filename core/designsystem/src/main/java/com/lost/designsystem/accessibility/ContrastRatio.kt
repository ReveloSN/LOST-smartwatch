package com.lost.designsystem.accessibility

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

/** Calcula el contraste WCAG 2.x entre dos colores (de 1:1 a 21:1). */
object ContrastRatio {
    /** Mínimo WCAG AA para texto normal. */
    const val WCAG_AA_NORMAL_TEXT = 4.5f

    private const val LUMINANCE_OFFSET = 0.05f

    fun between(foreground: Color, background: Color): Float {
        val lighter = maxOf(foreground.luminance(), background.luminance())
        val darker = minOf(foreground.luminance(), background.luminance())
        return (lighter + LUMINANCE_OFFSET) / (darker + LUMINANCE_OFFSET)
    }

    fun meetsAa(foreground: Color, background: Color): Boolean = between(foreground, background) >= WCAG_AA_NORMAL_TEXT
}
