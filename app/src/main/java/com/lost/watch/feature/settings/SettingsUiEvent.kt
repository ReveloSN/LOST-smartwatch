package com.lost.watch.feature.settings

import com.lost.domain.model.Sensitivity

/** Cambios del usuario en Ajustes. */
sealed interface SettingsUiEvent {
    data class ChangeSensitivity(val sensitivity: Sensitivity) : SettingsUiEvent

    data object ToggleVibration : SettingsUiEvent

    data object ToggleLargeText : SettingsUiEvent

    data object DeleteLearnedData : SettingsUiEvent
}
