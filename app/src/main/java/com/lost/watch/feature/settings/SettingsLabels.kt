package com.lost.watch.feature.settings

import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.watch.R

/** Etiquetas humanas de cada opción de Ajustes. */
internal object SettingsLabels {
    fun of(sensitivity: Sensitivity): Int = when (sensitivity) {
        Sensitivity.LOW -> R.string.sensitivity_low
        Sensitivity.MEDIUM -> R.string.sensitivity_medium
        Sensitivity.HIGH -> R.string.sensitivity_high
    }

    fun of(vibration: VibrationIntensity): Int = when (vibration) {
        VibrationIntensity.SOFT -> R.string.vibration_soft
        VibrationIntensity.STRONG -> R.string.vibration_strong
    }

    fun onOff(isEnabled: Boolean): Int = if (isEnabled) R.string.settings_on else R.string.settings_off
}
