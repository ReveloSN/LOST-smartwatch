package com.lost.designsystem.components.molecules

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.wear.compose.material3.Text
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.theme.LostTypography
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Un conteo como en el mockup 03.1: el número grande y "de 5 días" más pequeño y en gris, en la misma línea.
 * Si el idioma lo alarga (pt, en-XA, texto grande) pasa a una segunda línea en vez de cortarse con "…".
 * TalkBack lee [description], la frase completa ("3 de 5 días").
 */
@Composable
fun CountOfTotal(count: String, ofTotal: String, description: String, modifier: Modifier = Modifier) {
    val text = buildAnnotatedString {
        withStyle(LostTypography.number.toSpanStyle().copy(color = SemanticTokens.Text.primary)) { append(count) }
        append(" ")
        withStyle(SpanStyle(color = SemanticTokens.Text.secondary)) { append(ofTotal) }
    }
    Text(
        text = text,
        modifier = modifier.clearAndSetSemantics { contentDescription = description },
        style = LostTypography.title,
        textAlign = TextAlign.Center,
        maxLines = MAX_LINES,
        overflow = TextOverflow.Ellipsis
    )
}

private const val MAX_LINES = 2

@LostDevicePreviews
@Composable
private fun CountOfTotalPreview() {
    LostPreviewSurface { CountOfTotal(count = "3", ofTotal = "de 5 días", description = "3 de 5 días") }
}
