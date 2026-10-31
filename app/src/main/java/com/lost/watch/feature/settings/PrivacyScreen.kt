package com.lost.watch.feature.settings

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.watch.R

/** 04.4 · Privacidad: procesamiento en el reloj. Borrar exige mantener presionado. */
@Composable
fun PrivacyScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PrivacyContent(state) { viewModel.onEvent(SettingsUiEvent.DeleteLearnedData) }
}

@Composable
private fun PrivacyContent(state: SettingsUiState, onDelete: () -> Unit) {
    WatchScreenTemplate {
        LostIcon(Icons.Rounded.Lock, null, tint = SemanticTokens.Status.onRoute)
        LostText(stringResource(R.string.privacy_on_device), role = LostTextRole.TITLE)
        LostText(
            stringResource(R.string.privacy_sharing),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostText(
            stringResource(R.string.privacy_map),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        DeleteLearningAction(state.hasDeletedLearning, onDelete)
    }
}

@LostDevicePreviews
@Composable
private fun PrivacyPreview() {
    val state =
        SettingsUiState(
            Sensitivity.MEDIUM,
            VibrationIntensity.STRONG,
            false,
            35,
            isDemoAvailable = false,
            hasDeletedLearning = false
        )
    LostTheme { PrivacyContent(state) {} }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DeleteLearningAction(hasDeleted: Boolean, onDelete: () -> Unit) {
    if (hasDeleted) {
        LostText(
            stringResource(R.string.privacy_deleted),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Status.onRoute
        )
    } else {
        LostText(
            text = stringResource(R.string.privacy_delete),
            role = LostTextRole.TITLE,
            color = SemanticTokens.Status.help,
            modifier = Modifier
                .heightIn(min = ComponentTokens.Touch.minTarget)
                .combinedClickable(role = Role.Button, onClick = {}, onLongClick = onDelete)
        )
        LostText(
            stringResource(R.string.privacy_delete_hint),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}
