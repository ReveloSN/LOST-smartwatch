package com.lost.designsystem.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/** Flecha que gira hacia el destino + distancia grande + detalle ("≈ 3 min caminando"). */
@Composable
fun DistanceIndicator(content: DistanceContent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.semantics(mergeDescendants = true) {},
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        LostIcon(
            imageVector = Icons.Rounded.Navigation,
            contentDescription = content.arrowDescription,
            modifier = Modifier.rotate(content.arrowRotationDegrees),
            tint = SemanticTokens.Status.of(content.tone),
            size = ComponentTokens.Icon.hero
        )
        LostText(text = content.distance, role = LostTextRole.NUMBER)
        LostText(text = content.detail, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
    }
}

@LostDevicePreviews
@Composable
private fun DistanceIndicatorPreview() {
    LostPreviewSurface {
        DistanceIndicator(DistanceContent(PreviewSamples.DISTANCE, PreviewSamples.DISTANCE_DETAIL, 35f))
    }
}
