package com.lost.designsystem.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Icono de estado dentro de un círculo. Tintado (mockup F.4b) marca el tono sin pintar la pantalla;
 * relleno (mockup V.3, "Entendido.") es la confirmación más visible.
 */
@Composable
fun StatusIconBadge(icon: ImageVector, tone: StatusTone, modifier: Modifier = Modifier, isFilled: Boolean = false) {
    val container = if (isFilled) SemanticTokens.Status.of(tone) else SemanticTokens.Surface.tinted(tone)
    val tint = if (isFilled) SemanticTokens.Text.onAccent else SemanticTokens.Status.of(tone)
    Box(
        modifier = modifier
            .size(ComponentTokens.IconBadge.size)
            .background(container, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        LostIcon(icon, null, tint = tint, size = ComponentTokens.Icon.large)
    }
}

@LostDevicePreviews
@Composable
private fun StatusIconBadgePreview() {
    LostPreviewSurface { StatusIconBadge(Icons.Rounded.ShareLocation, StatusTone.HELP) }
}
