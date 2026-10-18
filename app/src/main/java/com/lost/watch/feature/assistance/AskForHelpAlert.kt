package com.lost.watch.feature.assistance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ActionButtonContent
import com.lost.designsystem.components.molecules.DualActionButtons
import com.lost.designsystem.components.templates.AlertHeader
import com.lost.designsystem.components.templates.AlertTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.R
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.Tone
import com.lost.watch.ui.explanationSentence

private const val MEDIUM_ARC = 0.6f
private const val HIGH_ARC = 1f

/**
 * F.2 / E.3 · Vibrar y preguntar. El tono cambia con el nivel: atento ("¿Necesitas ayuda?")
 * o firme y claro ("Creo que necesitas ayuda. Te guío y aviso a tu contacto.").
 */
@Composable
fun AskForHelpAlert(state: AssistanceUiState.AskForHelp, onEvent: (AssistanceUiEvent) -> Unit) {
    val arc = if (state.status == StatusPresentation.HELP) HIGH_ARC else MEDIUM_ARC
    AlertTemplate(
        header = AlertHeader(
            icon = Icons.Rounded.Vibration,
            context = state.tone.context?.let { stringResource(it) },
            question = stringResource(state.tone.question),
            tone = state.status.tone
        ),
        arcFraction = arc
    ) {
        DualActionButtons(
            primary = ActionButtonContent(stringResource(R.string.assist_yes_guide)) {
                onEvent(AssistanceUiEvent.GuideMe)
            },
            secondary = ActionButtonContent(stringResource(R.string.assist_im_fine)) {
                onEvent(AssistanceUiEvent.ImFine)
            }
        )
        if (state.reasons.isNotEmpty()) {
            LostText(
                explanationSentence(state.reasons),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun AskForHelpPreview() {
    val state = AssistanceUiState.AskForHelp(
        StatusPresentation.DEVIATION,
        Tone.ATTENTIVE,
        listOf(DeviationReason.LeftUsualRoute(Meters(150.0)), DeviationReason.RepeatedArea(3))
    )
    LostTheme { AskForHelpAlert(state) {} }
}
