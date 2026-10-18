package com.lost.designsystem.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Nivel 1 — valores crudos de la marca (sección 05 de los mockups).
 *
 * Ningún componente debe usar estos valores directamente: siempre pasan por
 * [SemanticTokens] o [ComponentTokens]. Así un cambio de paleta toca un solo archivo.
 *
 * Los mockups están dibujados en píxeles de una pantalla de 454 px (≈ 2x). Por eso
 * los tamaños se convierten dividiendo entre dos y redondeando a la escala de Wear OS.
 */
object PrimitiveTokens {
    object Palette {
        val teal400 = Color(0xFF3EE6D2)
        val teal950 = Color(0xFF00211D)
        val amber300 = Color(0xFFFFC266)
        val coral400 = Color(0xFFFF7F6E)
        val neutral0 = Color(0xFF000000)
        val neutral800 = Color(0xFF1A1C1F)
        val neutral700 = Color(0xFF1E2023)
        val gray600 = Color(0xFF5D6267)
        val neutral400 = Color(0xFFA3A7AB)
        val neutral50 = Color(0xFFF4F4F2)
    }

    object Space {
        val space2 = 2.dp
        val space4 = 4.dp
        val space8 = 8.dp
        val space12 = 12.dp
        val space16 = 16.dp
        val space24 = 24.dp
    }

    object Radius {
        val md = 16.dp
        val lg = 28.dp
        val full = 9999.dp
    }

    object FontSize {
        val size12 = 12.sp
        val size14 = 14.sp
        val size16 = 16.sp
        val size18 = 18.sp
        val size28 = 28.sp
        val size44 = 44.sp
    }

    object Alpha {
        const val ALPHA_8 = 0.08f
        const val ALPHA_12 = 0.12f
        const val ALPHA_14 = 0.14f
        const val ALPHA_28 = 0.28f
        const val ALPHA_60 = 0.60f
    }

    object Motion {
        const val BREATHE_MILLIS = 4000
        const val TRANSITION_MILLIS = 600
        const val POP_MILLIS = 300
    }
}
