package com.lost.designsystem.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Punto de color del estado. Es decorativo: siempre va acompañado de icono y texto,
 * porque el estado nunca se comunica solo con color.
 */
@Composable
fun StatusDot(tone: StatusTone, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(ComponentTokens.StatusDot.size)
            .background(SemanticTokens.Status.of(tone), CircleShape)
    )
}

@LostDevicePreviews
@Composable
private fun StatusDotPreview() {
    LostPreviewSurface {
        Row(horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.sm)) {
            StatusTone.entries.forEach { StatusDot(tone = it) }
        }
    }
}
