package com.lost.designsystem.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/** Inicial de una persona en un círculo tintado (mockup 04.3). Decorativa: el nombre va al lado. */
@Composable
fun InitialAvatar(name: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(ComponentTokens.IconBadge.size)
            .background(SemanticTokens.Surface.tinted(StatusTone.ON_ROUTE), CircleShape)
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center
    ) {
        LostText(name.trim().take(1).uppercase(), role = LostTextRole.NUMBER, color = SemanticTokens.Status.onRoute)
    }
}

@LostDevicePreviews
@Composable
private fun InitialAvatarPreview() {
    LostPreviewSurface { InitialAvatar("Laura") }
}
