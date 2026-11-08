package com.lost.designsystem.components.atoms

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Etiqueta pequeña: "Sugerido" o el estado ("Todo en orden") en un solo bloque de color + icono + texto.
 * Con [onClick] se vuelve la puerta a "saber más": muestra una flecha y ocupa un área táctil de 48 dp.
 */
@Composable
fun LostChip(
    label: String,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    tone: StatusTone = StatusTone.ON_ROUTE,
    onClick: (() -> Unit)? = null
) {
    val touch = if (onClick != null) {
        Modifier
            .heightIn(min = ComponentTokens.Touch.minTarget)
            .clickable(role = Role.Button, onClick = onClick)
    } else {
        Modifier
    }
    Box(modifier = modifier.then(touch), contentAlignment = Alignment.Center) {
        ChipBody(label, icon, tone, isClickable = onClick != null)
    }
}

@Composable
private fun ChipBody(label: String, icon: ImageVector?, tone: StatusTone, isClickable: Boolean) {
    val color = SemanticTokens.Status.of(tone)
    Row(
        modifier = Modifier
            .semantics(mergeDescendants = true) {}
            .heightIn(min = ComponentTokens.Chip.height)
            .background(SemanticTokens.Surface.tinted(tone), RoundedCornerShape(SemanticTokens.Shape.pill))
            .padding(horizontal = ComponentTokens.Chip.horizontalPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        icon?.let {
            LostIcon(imageVector = it, contentDescription = null, tint = color, size = ComponentTokens.Icon.small)
        }
        LostText(text = label, role = LostTextRole.CAPTION, color = color)
        if (isClickable) {
            LostIcon(
                Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = null,
                tint = color,
                size = ComponentTokens.Icon.small
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun LostChipPreview() {
    LostPreviewSurface {
        LostChip(label = PreviewSamples.SUGGESTED, icon = Icons.Rounded.AutoAwesome)
    }
}

@LostDevicePreviews
@Composable
private fun LostChipClickablePreview() {
    LostPreviewSurface {
        LostChip(label = PreviewSamples.STATUS, icon = Icons.Rounded.CheckCircle, onClick = {})
    }
}
