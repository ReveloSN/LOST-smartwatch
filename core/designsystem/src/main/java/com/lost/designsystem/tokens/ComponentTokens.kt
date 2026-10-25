package com.lost.designsystem.tokens

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.lost.designsystem.tokens.PrimitiveTokens.Alpha

/**
 * Nivel 3 — decisiones de cada componente, construidas sobre [SemanticTokens].
 * Valores tomados de la lámina "05 Design system" (px de 454 / 2 ≈ dp).
 */
object ComponentTokens {
    object StatusRing {
        val strokeWidth: Dp = 7.dp
        const val TRACK_OPACITY: Float = Alpha.ALPHA_14
        val inset: Dp = 8.dp
        const val START_ANGLE: Float = -90f
        const val FULL_SWEEP: Float = 360f
        const val SEGMENT_GAP_DEGREES: Float = 6f
    }

    object RouteLine {
        const val HABITUAL_OPACITY: Float = Alpha.ALPHA_28
        val habitualWidth: Dp = 3.dp
        val todayWidth: Dp = 3.dp
        val divergencePulseRadius: Dp = 6.dp
        val userDotRadius: Dp = 5.dp
    }

    object PrimaryButton {
        val container: Color = SemanticTokens.Status.onRoute
        val content: Color = SemanticTokens.Text.onAccent
        val height: Dp = 52.dp
    }

    object SecondaryButton {
        val container: Color = SemanticTokens.Background.raised
        val content: Color = SemanticTokens.Text.primary
        val height: Dp = 52.dp
    }

    object Touch {
        val minTarget: Dp = 48.dp
    }

    object AlertCard {
        val padding: Dp = SemanticTokens.Spacing.md

        fun surface(tone: StatusTone): Color = SemanticTokens.Surface.tinted(tone)
    }

    object Card {
        val surface: Color = SemanticTokens.Surface.glass
        val padding: Dp = SemanticTokens.Spacing.md
    }

    object Chip {
        val height: Dp = 24.dp
        val horizontalPadding: Dp = SemanticTokens.Spacing.sm
    }

    object Icon {
        val small: Dp = 16.dp
        val medium: Dp = 24.dp
        val large: Dp = 32.dp
        val hero: Dp = 48.dp
    }

    object StatusDot {
        val size: Dp = 8.dp
    }

    object MapScreen {
        /** El mapa ocupa el centro; arriba queda el estado y abajo el mensaje, sin tapar las rutas. */
        const val MAP_HEIGHT_FRACTION: Float = 0.56f
        const val TEXT_WIDTH_FRACTION: Float = 0.64f
        val edgePadding: Dp = SemanticTokens.Spacing.xl
    }

    object SafeArea {
        /** 320 px de ancho útil sobre 454 px: el texto nunca toca la curva del reloj. */
        const val ROUND_TEXT_WIDTH_FRACTION: Float = 0.70f
        const val SQUARE_TEXT_WIDTH_FRACTION: Float = 0.88f
        val ringInset: Dp = 8.dp
    }
}
