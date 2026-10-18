package com.lost.designsystem.accessibility

import androidx.compose.ui.graphics.Color
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

class ContrastRatioTest {
    @Test
    fun `negro sobre blanco da el maximo de 21 a 1`() {
        assertEquals(21f, ContrastRatio.between(Color.Black, Color.White), 0.01f)
    }

    @TestFactory
    fun `todo color de texto o estado cumple WCAG AA sobre el fondo OLED`() = textOnBlack.map { (name, color) ->
        DynamicTest.dynamicTest(name) {
            val ratio = ContrastRatio.between(color, SemanticTokens.Background.base)
            assertTrue(ratio >= ContrastRatio.WCAG_AA_NORMAL_TEXT, "$name tiene contraste $ratio")
        }
    }

    @Test
    fun `el texto del boton principal cumple WCAG AA sobre aqua`() {
        assertTrue(
            ContrastRatio.meetsAa(ComponentTokens.PrimaryButton.content, ComponentTokens.PrimaryButton.container)
        )
    }

    @Test
    fun `el texto del boton secundario cumple WCAG AA sobre su superficie`() {
        assertTrue(
            ContrastRatio.meetsAa(ComponentTokens.SecondaryButton.content, ComponentTokens.SecondaryButton.container)
        )
    }

    private val textOnBlack = listOf(
        "text.primary" to SemanticTokens.Text.primary,
        "text.secondary" to SemanticTokens.Text.secondary,
        "status.onRoute" to SemanticTokens.Status.onRoute,
        "status.deviation" to SemanticTokens.Status.deviation,
        "status.help" to SemanticTokens.Status.help
    )
}
