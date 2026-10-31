package com.lost.watch.feature.settings

import com.lost.domain.model.Sensitivity

/** Cambios del usuario en Ajustes. */
sealed interface SettingsUiEvent {
    data class ChangeSensitivity(val sensitivity: Sensitivity) : SettingsUiEvent

    data object ToggleVibration : SettingsUiEvent

    data object ToggleLargeText : SettingsUiEvent

    /** "Mapa real (usa internet)": apagado, LOST no pide nada a OpenStreetMap. */
    data object ToggleRealMap : SettingsUiEvent

    data object DeleteLearnedData : SettingsUiEvent
}
