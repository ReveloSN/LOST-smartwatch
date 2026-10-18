package com.lost.designsystem.components.templates

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Plantilla base de toda pantalla: fondo OLED, anillo opcional pegado al borde y contenido
 * centrado dentro de la zona segura (70 % del ancho en reloj redondo, 88 % en cuadrado).
 * Si el texto crece (idiomas, letra grande) el contenido se desplaza en vez de cortarse.
 */
@Composable
fun WatchScreenTemplate(
    modifier: Modifier = Modifier,
    ring: @Composable () -> Unit = {},
    content: @Composable ColumnScope.() -> Unit
) {
    val widthFraction = if (LocalConfiguration.current.isScreenRound) {
        ComponentTokens.SafeArea.ROUND_TEXT_WIDTH_FRACTION
    } else {
        ComponentTokens.SafeArea.SQUARE_TEXT_WIDTH_FRACTION
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(SemanticTokens.Background.base),
        contentAlignment = Alignment.Center
    ) {
        ring()
        Column(
            modifier = Modifier
                .fillMaxWidth(widthFraction)
                .fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(vertical = SemanticTokens.Spacing.xl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.sm, Alignment.CenterVertically),
            content = content
        )
    }
}

@LostDevicePreviews
@Composable
private fun WatchScreenTemplatePreview() {
    LostPreviewSurface {
        WatchScreenTemplate {
            LostIcon(Icons.Rounded.CheckCircle, contentDescription = null, tint = SemanticTokens.Status.onRoute)
            LostText(text = PreviewSamples.STATUS, role = LostTextRole.QUESTION)
            LostPrimaryButton(label = PreviewSamples.PRIMARY_ACTION, onClick = {})
        }
    }
}
