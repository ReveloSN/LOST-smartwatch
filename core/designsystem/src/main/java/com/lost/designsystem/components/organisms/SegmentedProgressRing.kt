package com.lost.designsystem.components.organisms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
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
 * Anillo partido en segmentos: "Aprendiendo tu rutina 4 de 5" o "Tu semana L M M J V".
 * Un segmento marcado (FLAGGED) usa el color de desviación, por ejemplo un día con desvío.
 */
@Composable
fun SegmentedProgressRing(
    segments: List<SegmentState>,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit = {}
) {
    val painter = with(LocalDensity.current) {
        RingArcPainter(StatusRing.strokeWidth.toPx(), StatusRing.inset.toPx())
    }
    val segmentSweep = StatusRing.FULL_SWEEP / segments.size.coerceAtLeast(1)
    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            segments.forEachIndexed { index, state ->
                val arc = RingArc(
                    color = colorFor(state),
                    startAngle = StatusRing.START_ANGLE + index * segmentSweep + StatusRing.SEGMENT_GAP_DEGREES / 2,
                    sweepAngle = segmentSweep - StatusRing.SEGMENT_GAP_DEGREES
                )
                with(painter) { drawArc(arc) }
            }
        }
        content()
    }
}

private fun colorFor(state: SegmentState) = when (state) {
    SegmentState.EMPTY -> SemanticTokens.Status.onRoute.copy(alpha = StatusRing.TRACK_OPACITY)
    SegmentState.DONE -> SemanticTokens.Status.onRoute
    SegmentState.FLAGGED -> SemanticTokens.Status.of(StatusTone.DEVIATION)
}

@LostDevicePreviews
@Composable
private fun SegmentedProgressRingPreview() {
    LostPreviewSurface {
        SegmentedProgressRing(
            segments = List(4) { SegmentState.DONE } + SegmentState.EMPTY
        ) {
            LostText(text = PreviewSamples.LEARNING_PROGRESS, role = LostTextRole.DISPLAY)
        }
    }
}

@LostDevicePreviews
@Composable
private fun SegmentedProgressRingEmptyPreview() {
    LostPreviewSurface {
        SegmentedProgressRing(
            segments =
                listOf(SegmentState.DONE, SegmentState.FLAGGED) + List(3) { SegmentState.EMPTY }
        )
    }
}
