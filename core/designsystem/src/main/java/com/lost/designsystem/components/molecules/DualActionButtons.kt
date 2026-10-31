package com.lost.designsystem.components.molecules

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Botón doble Sí/No. Apilados (no lado a lado): en un reloj redondo el ancho
 * útil es poco y cada respuesta merece un área táctil completa. La segunda va
 * solo con texto: una píldora rellena abajo choca con la curva de la pantalla,
 * pero su área táctil sigue siendo de 52 dp.
 */
@Composable
fun DualActionButtons(primary: ActionButtonContent, secondary: ActionButtonContent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        LostPrimaryButton(label = primary.label, onClick = primary.onClick)
        LostSecondaryButton(
            label = secondary.label,
            onClick = secondary.onClick,
            style = SecondaryButtonStyle.TEXT_ONLY
        )
    }
}

@LostDevicePreviews
@Composable
private fun DualActionButtonsPreview() {
    LostPreviewSurface {
        DualActionButtons(
            primary = ActionButtonContent(PreviewSamples.PRIMARY_ACTION) {},
            secondary = ActionButtonContent(PreviewSamples.SECONDARY_ACTION) {}
        )
    }
}

@LostDevicePreviews
@Composable
private fun DualActionButtonsLongPreview() {
    LostPreviewSurface {
        DualActionButtons(
            primary = ActionButtonContent(PreviewSamples.LONG_ACTION) {},
            secondary = ActionButtonContent(PreviewSamples.LONG_ACTION) {}
        )
    }
}
