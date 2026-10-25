package com.lost.designsystem.components.atoms

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.Hyphens
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.wear.compose.material3.Text
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.theme.LostTypography
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Texto de LOST. Limita las líneas según el rol (títulos 2, cuerpo 3) y corta con "…"
 * en vez de recortar a mitad de palabra, como piden las reglas de texto de i18n.
 */
@Composable
fun LostText(
    text: String,
    modifier: Modifier = Modifier,
    role: LostTextRole = LostTextRole.BODY,
    color: Color = SemanticTokens.Text.primary,
    textAlign: TextAlign = TextAlign.Center
) {
    Text(
        text = text,
        modifier = modifier,
        style = role.style.copy(
            lineBreak = LineBreak.Paragraph,
            hyphens = Hyphens.None
        ),
        color = color,
        textAlign = textAlign,
        maxLines = role.maxLines,
        overflow = TextOverflow.Ellipsis
    )
}

@LostDevicePreviews
@Composable
private fun LostTextPreview() {
    LostPreviewSurface {
        LostText(text = PreviewSamples.QUESTION, role = LostTextRole.QUESTION)
    }
}

@LostDevicePreviews
@Composable
private fun LostTextLongPreview() {
    LostPreviewSurface {
        LostText(text = PreviewSamples.LONG_ROUTINE_TITLE.repeat(3), role = LostTextRole.TITLE)
    }
}
