package com.lost.designsystem.components.atoms

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.wear.compose.material3.Icon
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Icono redondeado de LOST. `contentDescription` es obligatorio: si el icono es decorativo
 * (porque al lado ya hay texto), se pasa `null` de forma explícita.
 */
@Composable
fun LostIcon(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    tint: Color = SemanticTokens.Text.primary,
    size: Dp = ComponentTokens.Icon.medium
) {
    Icon(
        imageVector = imageVector,
        contentDescription = contentDescription,
        modifier = modifier.size(size),
        tint = tint
    )
}

@LostDevicePreviews
@Composable
private fun LostIconPreview() {
    LostPreviewSurface {
        LostIcon(
            imageVector = Icons.Rounded.CheckCircle,
            contentDescription = PreviewSamples.STATUS,
            tint = SemanticTokens.Status.onRoute,
            size = ComponentTokens.Icon.hero
        )
    }
}
