package com.lost.designsystem.components.organisms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
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

/**
 * Tarjeta de alerta sobre superficie tintada (12 %). Nunca pinta la pantalla completa de rojo.
 * Es región viva: TalkBack la lee en cuanto aparece.
 */
@Composable
fun AlertCard(content: AlertContent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite }
            .background(
                ComponentTokens.AlertCard.surface(content.tone),
                RoundedCornerShape(SemanticTokens.Shape.container)
            )
            .padding(ComponentTokens.AlertCard.padding),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        LostIcon(
            imageVector = content.icon,
            contentDescription = null,
            tint = SemanticTokens.Status.of(content.tone),
            size = ComponentTokens.Icon.large
        )
        LostText(text = content.title, role = LostTextRole.TITLE)
        content.message?.let {
            LostText(text = it, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        }
    }
}

@LostDevicePreviews
@Composable
private fun AlertCardPreview() {
    LostPreviewSurface {
        AlertCard(
            AlertContent(
                Icons.Rounded.ShareLocation,
                PreviewSamples.ALERT_TITLE,
                PreviewSamples.ALERT_MESSAGE,
                StatusTone.ON_ROUTE
            )
        )
    }
}

@LostDevicePreviews
@Composable
private fun AlertCardErrorPreview() {
    LostPreviewSurface {
        AlertCard(
            AlertContent(
                Icons.Rounded.LocationOff,
                PreviewSamples.ERROR_TITLE,
                PreviewSamples.ERROR_MESSAGE,
                StatusTone.HELP
            )
        )
    }
}
