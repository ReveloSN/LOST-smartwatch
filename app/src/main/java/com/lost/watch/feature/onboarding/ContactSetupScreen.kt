package com.lost.watch.feature.onboarding

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/** 01.3 · Elegir contacto autorizado: permiso explícito y revocable. */
@Composable
fun ContactSetupScreen(onFinished: () -> Unit, viewModel: OnboardingViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ContactSetupContent(state) {
        viewModel.onEvent(OnboardingUiEvent.Finish)
        onFinished()
    }
}

@Composable
private fun ContactSetupContent(state: OnboardingUiState, onFinish: () -> Unit) {
    WatchScreenTemplate {
        LostText(stringResource(R.string.onboarding_contact_title), role = LostTextRole.QUESTION)
        val name = state.suggestedContactName
        if (name != null) {
            LostListItem(
                ListItemContent(Icons.Rounded.CheckCircle, name, state.suggestedContactRelationship),
                onClick = onFinish
            )
            LostPrimaryButton(label = stringResource(R.string.onboarding_choose_contact, name), onClick = onFinish)
            LostText(
                stringResource(R.string.onboarding_contact_footnote),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
        } else {
            LostText(stringResource(R.string.onboarding_no_contact), color = SemanticTokens.Text.secondary)
            LostPrimaryButton(label = stringResource(R.string.onboarding_continue), onClick = onFinish)
        }
    }
}

@LostDevicePreviews
@Composable
private fun ContactSetupPreview() {
    LostTheme { ContactSetupContent(OnboardingUiState("Laura", "Hermana"), onFinish = {}) }
}

@LostDevicePreviews
@Composable
private fun ContactSetupEmptyPreview() {
    LostTheme { ContactSetupContent(OnboardingUiState(null, null), onFinish = {}) }
}
