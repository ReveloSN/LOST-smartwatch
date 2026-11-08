package com.lost.watch.feature.settings

import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity

/**
 * Ajustes. [habitualRoutineMinutes] permite mostrar el ejemplo de sensibilidad
 * con la ruta real del usuario ("tu ruta de 35 min").
 */
data class SettingsUiState(
    val sensitivity: Sensitivity,
    val vibration: VibrationIntensity,
    val isLargeTextEnabled: Boolean,
    val habitualRoutineMinutes: Int?,
    val isDemoAvailable: Boolean,
    val hasDeletedLearning: Boolean,
    val isRealMapEnabled: Boolean = true
)
