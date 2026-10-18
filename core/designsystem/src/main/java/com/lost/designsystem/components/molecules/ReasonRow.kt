package com.lost.designsystem.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Cached
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/** Fila de "Lo que noto hoy": solo aparece cuando la señal se activó. */
@Composable
fun ReasonRow(content: ReasonContent, modifier: Modifier = Modifier, tone: StatusTone = StatusTone.DEVIATION) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .semantics(mergeDescendants = true) {},
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.sm)
    ) {
        LostIcon(imageVector = content.icon, contentDescription = null, tint = SemanticTokens.Status.of(tone))
        Column {
            LostText(text = content.headline, role = LostTextRole.TITLE, textAlign = TextAlign.Start)
            LostText(
                text = content.detail,
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary,
                textAlign = TextAlign.Start
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun ReasonRowPreview() {
    LostPreviewSurface {
        ReasonRow(ReasonContent(Icons.Rounded.Cached, PreviewSamples.REASON_TITLE, PreviewSamples.REASON_DETAIL))
    }
}
