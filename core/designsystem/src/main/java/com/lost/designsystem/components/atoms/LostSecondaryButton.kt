package com.lost.designsystem.components.atoms

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens

/** Acción secundaria. Mismo alto táctil que la principal para no penalizar a quien la elige. */
@Composable
fun LostSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: SecondaryButtonStyle = SecondaryButtonStyle.FILLED
) {
    val container = when (style) {
        SecondaryButtonStyle.FILLED -> ComponentTokens.SecondaryButton.container
        SecondaryButtonStyle.TEXT_ONLY -> SemanticTokens.Background.base
    }
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = ComponentTokens.SecondaryButton.height),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = ComponentTokens.SecondaryButton.content
        ),
        // Sin píldora el texto puede crecer: en otros idiomas pasa a una tercera línea en vez de cortarse.
        label = { ButtonLabel(label, if (style == SecondaryButtonStyle.TEXT_ONLY) TEXT_ONLY_MAX_LINES else 2) }
    )
}

private const val TEXT_ONLY_MAX_LINES = 3

@LostDevicePreviews
@Composable
private fun LostSecondaryButtonPreview() {
    LostPreviewSurface {
        LostSecondaryButton(label = PreviewSamples.SECONDARY_ACTION, onClick = {})
    }
}

@LostDevicePreviews
@Composable
private fun LostSecondaryButtonTextOnlyPreview() {
    LostPreviewSurface {
        LostSecondaryButton(label = PreviewSamples.LONG_ACTION, onClick = {}, style = SecondaryButtonStyle.TEXT_ONLY)
    }
}
