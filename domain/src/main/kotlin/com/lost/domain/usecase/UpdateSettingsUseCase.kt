package com.lost.domain.usecase

import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.domain.port.SettingsWriter
import javax.inject.Inject

/** Cambios que el usuario hace en Ajustes y al terminar la bienvenida. */
class UpdateSettingsUseCase @Inject constructor(private val writer: SettingsWriter) {
    suspend fun changeSensitivity(sensitivity: Sensitivity) = writer.update { it.copy(sensitivity = sensitivity) }

    suspend fun changeVibration(intensity: VibrationIntensity) =
        writer.update { it.copy(vibrationIntensity = intensity) }

    suspend fun changeLargeText(isEnabled: Boolean) = writer.update { it.copy(isLargeTextEnabled = isEnabled) }

    suspend fun changeRealMap(isEnabled: Boolean) = writer.update { it.copy(isRealMapEnabled = isEnabled) }

    suspend fun completeOnboarding() = writer.update { it.copy(isOnboardingCompleted = true) }
}
