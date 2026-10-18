package com.lost.designsystem.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.School
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

/** Tarjeta de recorrido ("Casa → U · L–V · 6:40 · 35 min") sobre superficie de vidrio. */
@Composable
fun RoutineCard(content: RoutineCardContent, onClick: () -> Unit, modifier: Modifier = Modifier) {
    LostListItem(ListItemContent(content.icon, content.title, content.detail), onClick = onClick, modifier = modifier)
}

@LostDevicePreviews
@Composable
private fun RoutineCardPreview() {
    LostPreviewSurface {
        RoutineCard(
            RoutineCardContent(Icons.Rounded.School, PreviewSamples.ROUTINE_TITLE, PreviewSamples.ROUTINE_DETAIL),
            onClick = {}
        )
    }
}

@LostDevicePreviews
@Composable
private fun RoutineCardLongPreview() {
    LostPreviewSurface {
        RoutineCard(
            RoutineCardContent(Icons.Rounded.School, PreviewSamples.LONG_ROUTINE_TITLE, PreviewSamples.ROUTINE_DETAIL),
            onClick = {}
        )
    }
}
