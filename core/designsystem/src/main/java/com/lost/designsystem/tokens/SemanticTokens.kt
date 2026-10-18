package com.lost.designsystem.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.lost.designsystem.tokens.PrimitiveTokens.Alpha
import com.lost.designsystem.tokens.PrimitiveTokens.Palette
import com.lost.designsystem.tokens.PrimitiveTokens.Radius
import com.lost.designsystem.tokens.PrimitiveTokens.Space

/**
 * Nivel 2 — tokens con intención. Dicen para qué se usa un color o un espacio,
 * no cuál es. Los componentes leen de aquí o de [ComponentTokens].
 */
object SemanticTokens {
    object Status {
        val onRoute: Color = Palette.teal400
        val deviation: Color = Palette.amber300
        val help: Color = Palette.coral400

        fun of(tone: StatusTone): Color = when (tone) {
            StatusTone.ON_ROUTE -> onRoute
            StatusTone.DEVIATION -> deviation
            StatusTone.HELP -> help
        }
    }

    object Route {
        val habitual: Color = Palette.neutral50.copy(alpha = Alpha.ALPHA_28)

        /** La ruta de hoy siempre toma el color del estado actual. */
        fun today(tone: StatusTone): Color = Status.of(tone)
    }

    object Background {
        val base: Color = Palette.neutral0
        val raised: Color = Palette.neutral700
    }

    object Surface {
        val glass: Color = Palette.neutral50.copy(alpha = Alpha.ALPHA_8)
        val outline: Color = Palette.gray600

        fun tinted(tone: StatusTone): Color = Status.of(tone).copy(alpha = Alpha.ALPHA_12)
    }

    object Text {
        val primary: Color = Palette.neutral50
        val secondary: Color = Palette.neutral400
        val onAccent: Color = Palette.teal950
    }

    object Spacing {
        val hairline: Dp = Space.space2
        val xs: Dp = Space.space4
        val sm: Dp = Space.space8
        val md: Dp = Space.space12
        val lg: Dp = Space.space16
        val xl: Dp = Space.space24
    }

    object Shape {
        val card: Dp = Radius.md
        val container: Dp = Radius.lg
        val pill: Dp = Radius.full
    }

    object Motion {
        const val BREATHE_MILLIS = PrimitiveTokens.Motion.BREATHE_MILLIS
        const val TRANSITION_MILLIS = PrimitiveTokens.Motion.TRANSITION_MILLIS
        const val POP_MILLIS = PrimitiveTokens.Motion.POP_MILLIS
        const val BREATHE_MIN_ALPHA: Float = Alpha.ALPHA_60
    }
}
