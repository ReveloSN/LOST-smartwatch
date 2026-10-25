package com.lost.designsystem.components.templates

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ActionButtonContent
import com.lost.designsystem.components.molecules.DualActionButtons
import com.lost.designsystem.components.organisms.ProbabilityArc
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.preview.LostPreviewSurface
import com.lost.designsystem.preview.PreviewSamples
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone

/**
 * Plantilla de la capa de alerta: arco perimetral del nivel, icono, contexto, pregunta
 * y las acciones. Se superpone a cualquier pantalla y TalkBack la anuncia.
 */
@Composable
fun AlertTemplate(
    header: AlertHeader,
    arcFraction: Float,
    modifier: Modifier = Modifier,
    actions: @Composable ColumnScope.() -> Unit
) {
    WatchScreenTemplate(
        modifier = modifier,
        ring = { ProbabilityArc(tone = header.tone, fraction = arcFraction) }
    ) {
        LostIcon(
            imageVector = header.icon,
            contentDescription = null,
            tint = SemanticTokens.Status.of(header.tone),
            size = ComponentTokens.Icon.medium
        )
        header.context?.let {
            LostText(text = it, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        }
        LostText(
            text = header.question,
            role = LostTextRole.QUESTION,
            modifier = Modifier.semantics { liveRegion = LiveRegionMode.Assertive }
        )
        Spacer(modifier = Modifier.height(SemanticTokens.Spacing.xs))
        actions()
    }
}

@LostDevicePreviews
@Composable
private fun AlertTemplatePreview() {
    LostPreviewSurface {
        AlertTemplate(
            header = AlertHeader(
                Icons.Rounded.Vibration,
                PreviewSamples.REASON_DETAIL,
                PreviewSamples.QUESTION,
                StatusTone.DEVIATION
            ),
            arcFraction = 0.6f
        ) {
            DualActionButtons(
                primary = ActionButtonContent(PreviewSamples.PRIMARY_ACTION) {},
                secondary = ActionButtonContent(PreviewSamples.SECONDARY_ACTION) {}
            )
        }
    }
}
