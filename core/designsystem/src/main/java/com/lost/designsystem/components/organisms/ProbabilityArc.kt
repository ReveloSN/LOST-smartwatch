package com.lost.designsystem.components.organisms

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens.StatusRing
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Arco perimetral que muestra cuánto preocupa la situación (o cuánto falta en una cuenta regresiva).
 * El usuario nunca ve un porcentaje: ve un arco que se llena, un color, un icono y un texto.
 */
@Composable
fun ProbabilityArc(
    tone: StatusTone,
    fraction: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val animatedFraction by animateFloatAsState(
        targetValue = fraction.coerceIn(0f, 1f),
        animationSpec = tween(SemanticTokens.Motion.TRANSITION_MILLIS),
        label = "probabilityFraction"
    )
    val color = SemanticTokens.Status.of(tone)
    val painter = with(LocalDensity.current) {
        RingArcPainter(StatusRing.strokeWidth.toPx(), StatusRing.inset.toPx())
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            with(painter) {
                drawArc(RingArc(color.copy(alpha = StatusRing.TRACK_OPACITY)))
                drawArc(RingArc(color, sweepAngle = StatusRing.FULL_SWEEP * animatedFraction))
            }
        }
        content()
    }
}

@LostDevicePreviews
@Composable
private fun ProbabilityArcMediumPreview() {
    LostPreviewSurface {
        ProbabilityArc(tone = StatusTone.DEVIATION, fraction = 0.6f) {
            LostText(text = PreviewSamples.QUESTION, role = LostTextRole.QUESTION)
        }
    }
}

@LostDevicePreviews
@Composable
private fun ProbabilityArcHighPreview() {
    LostPreviewSurface {
        ProbabilityArc(tone = StatusTone.HELP, fraction = 1f)
    }
}
