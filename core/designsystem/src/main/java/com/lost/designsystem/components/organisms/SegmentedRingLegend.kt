package com.lost.designsystem.components.organisms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Leyenda del [SegmentedProgressRing]: una letra por segmento con su mismo color ("L M M J V").
 * TalkBack no lee letra por letra sino [description], que dice el estado de cada día en palabras.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SegmentedRingLegend(labels: List<SegmentLabel>, description: String, modifier: Modifier = Modifier) {
    // FlowRow: si las letras se alargan (otro alfabeto, en-XA) pasan a otra línea en vez de salirse del círculo.
    FlowRow(
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.sm, Alignment.CenterHorizontally)
    ) {
        labels.forEach { label ->
            LostText(label.text, role = LostTextRole.CAPTION, color = legendColorFor(label.state))
        }
    }
}

private fun legendColorFor(state: SegmentState) = when (state) {
    SegmentState.EMPTY -> SemanticTokens.Text.secondary
    SegmentState.DONE -> SemanticTokens.Status.onRoute
    SegmentState.FLAGGED -> SemanticTokens.Status.of(StatusTone.DEVIATION)
}

@LostDevicePreviews
@Composable
private fun SegmentedRingLegendPreview() {
    val states =
        listOf(SegmentState.DONE, SegmentState.FLAGGED, SegmentState.DONE, SegmentState.EMPTY, SegmentState.EMPTY)
    LostPreviewSurface {
        SegmentedRingLegend(
            labels = PreviewSamples.WEEK_LETTERS.zip(states, ::SegmentLabel),
            description = PreviewSamples.STATUS
        )
    }
}
