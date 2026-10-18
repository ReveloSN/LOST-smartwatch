package com.lost.watch.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.watch.R
import kotlin.math.roundToInt

/** 04.2 · Sensibilidad con un ejemplo concreto que cambia con la opción elegida. */
@Composable
fun SensitivityScreen(viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    SensitivityContent(state) { viewModel.onEvent(SettingsUiEvent.ChangeSensitivity(it)) }
}

@Composable
private fun SensitivityContent(state: SettingsUiState, onSelect: (Sensitivity) -> Unit) {
    WatchScreenTemplate {
        LostText(stringResource(R.string.settings_sensitivity), role = LostTextRole.TITLE)
        Column(verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)) {
            Sensitivity.entries.forEach { option ->
                val label = stringResource(SettingsLabels.of(option))
                if (option ==
                    state.sensitivity
                ) {
                    LostPrimaryButton(label, { onSelect(option) })
                } else {
                    LostSecondaryButton(label, { onSelect(option) })
                }
            }
        }
        val minutes = state.habitualRoutineMinutes
        val example = if (minutes != null) {
            stringResource(
                R.string.sensitivity_example,
                minutes,
                (minutes * state.sensitivity.toleranceFactor).roundToInt()
            )
        } else {
            stringResource(R.string.sensitivity_example_generic)
        }
        LostText(example, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
    }
}

@LostDevicePreviews
@Composable
private fun SensitivityPreview() {
    val state =
        SettingsUiState(
            Sensitivity.MEDIUM,
            VibrationIntensity.STRONG,
            false,
            35,
            isDemoAvailable = false,
            hasDeletedLearning = false
        )
    LostTheme { SensitivityContent(state) {} }
}
