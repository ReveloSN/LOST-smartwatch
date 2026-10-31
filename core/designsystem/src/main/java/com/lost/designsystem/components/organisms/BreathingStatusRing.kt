package com.lost.designsystem.components.organisms

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
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
 * Anillo de estado de la IA, pegado al borde del reloj.
 * - En ruta: el arco muestra el avance sobre la ruta de siempre y "respira".
 * - Posible desviación: el arco se detiene (sin respiración).
 * - Necesita ayuda: el anillo se completa.
 * El color cambia con una transición suave para no asustar.
 */
@Composable
fun BreathingStatusRing(
    tone: StatusTone,
    progress: Float,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val color by animateColorAsState(
        targetValue = SemanticTokens.Status.of(tone),
        animationSpec = tween(SemanticTokens.Motion.TRANSITION_MILLIS),
        label = "ringColor"
    )
    val breathingAlpha = rememberBreathingAlpha(isBreathing = tone == StatusTone.ON_ROUTE)
    val sweep = sweepFor(tone, progress)
    val painter = with(LocalDensity.current) {
        RingArcPainter(StatusRing.strokeWidth.toPx(), StatusRing.inset.toPx())
    }
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // El alfa se lee dentro de graphicsLayer: respirar solo cambia la capa, sin recomponer ni redibujar.
        Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = breathingAlpha.value }) {
            with(painter) {
                drawArc(RingArc(color.copy(alpha = StatusRing.TRACK_OPACITY)))
                drawArc(RingArc(color, sweepAngle = sweep))
            }
        }
        content()
    }
}

private fun sweepFor(tone: StatusTone, progress: Float): Float = when (tone) {
    StatusTone.HELP -> StatusRing.FULL_SWEEP
    else -> StatusRing.FULL_SWEEP * progress.coerceIn(0f, 1f)
}

@Composable
private fun rememberBreathingAlpha(isBreathing: Boolean): State<Float> {
    if (!isBreathing) return remember { mutableFloatStateOf(1f) }
    val transition = rememberInfiniteTransition(label = "breathe")
    return transition.animateFloat(
        initialValue = SemanticTokens.Motion.BREATHE_MIN_ALPHA,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(SemanticTokens.Motion.BREATHE_MILLIS / 2, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breatheAlpha"
    )
}

@LostDevicePreviews
@Composable
private fun BreathingStatusRingCalmPreview() {
    LostPreviewSurface {
        BreathingStatusRing(tone = StatusTone.ON_ROUTE, progress = 0.3f) {
            LostText(text = PreviewSamples.STATUS, role = LostTextRole.TITLE)
        }
    }
}

@LostDevicePreviews
@Composable
private fun BreathingStatusRingHelpPreview() {
    LostPreviewSurface {
        BreathingStatusRing(tone = StatusTone.HELP, progress = 0.3f) {
            LostText(text = PreviewSamples.QUESTION, role = LostTextRole.QUESTION)
        }
    }
}
