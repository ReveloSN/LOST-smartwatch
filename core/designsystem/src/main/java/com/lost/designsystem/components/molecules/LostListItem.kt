package com.lost.designsystem.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.wear.compose.material3.Card
import androidx.wear.compose.material3.CardDefaults
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens

/** Fila de lista circular: icono + título + valor ("Sensibilidad · Media"). Área táctil ≥ 48 dp. */
@Composable
fun LostListItem(
    content: ListItemContent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconTint: Color = SemanticTokens.Status.onRoute
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ComponentTokens.Touch.minTarget),
        shape = RoundedCornerShape(SemanticTokens.Shape.container),
        colors = CardDefaults.cardColors(containerColor = ComponentTokens.Card.surface)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.sm)
        ) {
            LostIcon(imageVector = content.icon, contentDescription = null, tint = iconTint)
            Column {
                LostText(text = content.title, role = LostTextRole.TITLE, textAlign = TextAlign.Start)
                content.detail?.let {
                    LostText(
                        text = it,
                        role = LostTextRole.CAPTION,
                        color = SemanticTokens.Text.secondary,
                        textAlign = TextAlign.Start
                    )
                }
            }
        }
    }
}

@LostDevicePreviews
@Composable
private fun LostListItemPreview() {
    LostPreviewSurface {
        LostListItem(
            ListItemContent(
                Icons.Rounded.Tune,
                PreviewSamples.ROUTINE_TITLE,
                PreviewSamples.ROUTINE_DETAIL
            ),
            onClick = {
            }
        )
    }
}
