package com.lost.designsystem.theme

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.lost.designsystem.tokens.PrimitiveTokens.FontSize

/**
 * Escala tipográfica de los mockups (Display 96 · Número 48 · Pregunta 32 · Título 20 · Mínimo 16 px),
 * convertida a sp para Wear OS. Al usar sp respeta el "texto grande" del sistema.
 */
object LostTypography {
    /** Manrope en los mockups; mientras no se empaquete la fuente se usa la sans del sistema. */
    val brandFamily: FontFamily = FontFamily.SansSerif

    val display = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.Bold, fontSize = FontSize.size44)
    val number = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.Bold, fontSize = FontSize.size28)
    val question = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.SemiBold, fontSize = FontSize.size18)
    val title = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.SemiBold, fontSize = FontSize.size16)
    val body = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.Normal, fontSize = FontSize.size14)
    val caption = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.Medium, fontSize = FontSize.size12)
    val button = TextStyle(fontFamily = brandFamily, fontWeight = FontWeight.Bold, fontSize = FontSize.size14)
}
