package com.lost.designsystem.components.molecules

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens

/** Puntos que indican la sección actual. TalkBack lee la descripción ("Inicio, sección 1 de 5"). */
@Composable
fun PageIndicator(pageCount: Int, selectedPage: Int, description: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.semantics { contentDescription = description },
        horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        repeat(pageCount) { page ->
            val color = if (page == selectedPage) SemanticTokens.Text.primary else SemanticTokens.Surface.outline
            Box(modifier = Modifier.size(ComponentTokens.StatusDot.size).background(color, CircleShape))
        }
    }
}

@LostDevicePreviews
@Composable
private fun PageIndicatorPreview() {
    LostPreviewSurface {
        PageIndicator(pageCount = 5, selectedPage = 1, description = PreviewSamples.STATUS)
    }
}
