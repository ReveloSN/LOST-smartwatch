package com.lost.watch.feature.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/**
 * 01.3 · Elegir contacto autorizado: una pregunta y una acción (el botón ya nombra a la persona), y "Elegir otra
 * persona" para buscarla entre los contactos del reloj.
 */
@Composable
fun ContactSetupScreen(
    onFinished: () -> Unit,
    onChooseOther: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ContactSetupContent(state, onChooseOther) {
        viewModel.onEvent(OnboardingUiEvent.Finish)
        onFinished()
    }
}

@Composable
private fun ContactSetupContent(state: OnboardingUiState, onChooseOther: () -> Unit, onFinish: () -> Unit) {
    WatchScreenTemplate {
        LostText(stringResource(R.string.onboarding_contact_title), role = LostTextRole.QUESTION)
        val name = state.suggestedContactName
        if (name != null) {
            LostPrimaryButton(label = stringResource(R.string.onboarding_choose_contact, name), onClick = onFinish)
            LostSecondaryButton(
                stringResource(R.string.onboarding_choose_other),
                onChooseOther,
                style = SecondaryButtonStyle.TEXT_ONLY
            )
            LostText(
                stringResource(R.string.onboarding_contact_footnote),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
        } else {
            LostText(stringResource(R.string.onboarding_no_contact), color = SemanticTokens.Text.secondary)
            LostPrimaryButton(label = stringResource(R.string.contact_choose), onClick = onChooseOther)
            LostSecondaryButton(
                stringResource(R.string.onboarding_continue),
                onFinish,
                style = SecondaryButtonStyle.TEXT_ONLY
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun ContactSetupPreview() {
    LostTheme { ContactSetupContent(OnboardingUiState("Laura"), onChooseOther = {}, onFinish = {}) }
}

@LostDevicePreviews
@Composable
private fun ContactSetupEmptyPreview() {
    LostTheme { ContactSetupContent(OnboardingUiState(null), onChooseOther = {}, onFinish = {}) }
}
