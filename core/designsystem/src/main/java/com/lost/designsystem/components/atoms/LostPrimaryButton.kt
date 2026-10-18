package com.lost.designsystem.components.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.Text
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.theme.LostTypography
import com.lost.designsystem.tokens.ComponentTokens

/**
 * Acción principal de cada pantalla (aqua sobre negro). Ocupa todo el ancho seguro y
 * crece hasta dos líneas si el idioma lo necesita; nunca tiene un ancho fijo al texto.
 */
@Composable
fun LostPrimaryButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, icon: ImageVector? = null) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ComponentTokens.PrimaryButton.height),
        colors = ButtonDefaults.buttonColors(
            containerColor = ComponentTokens.PrimaryButton.container,
            contentColor = ComponentTokens.PrimaryButton.content,
            iconColor = ComponentTokens.PrimaryButton.content
        ),
        icon = icon?.let { vector ->
            { LostIcon(imageVector = vector, contentDescription = null, tint = ComponentTokens.PrimaryButton.content) }
        },
        label = { ButtonLabel(label) }
    )
}

@Composable
internal fun ButtonLabel(label: String) {
    Text(
        text = label,
        modifier = Modifier.fillMaxWidth(),
        style = LostTypography.button,
        textAlign = TextAlign.Center,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis
    )
}

@LostDevicePreviews
@Composable
private fun LostPrimaryButtonPreview() {
    LostPreviewSurface {
        LostPrimaryButton(label = PreviewSamples.PRIMARY_ACTION, onClick = {}, icon = Icons.Rounded.Navigation)
    }
}

@LostDevicePreviews
@Composable
private fun LostPrimaryButtonLongPreview() {
    LostPreviewSurface {
        LostPrimaryButton(label = PreviewSamples.LONG_ACTION, onClick = {})
    }
}
