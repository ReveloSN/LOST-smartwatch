package com.lost.domain.model

/** Preferencias del usuario (pantalla Ajustes). */
data class UserSettings(
    val sensitivity: Sensitivity,
    val vibrationIntensity: VibrationIntensity,
    val isLargeTextEnabled: Boolean,
    val isOnboardingCompleted: Boolean,
    val thresholds: PersonalThresholds
) {
    companion object {
        val DEFAULT = UserSettings(
            sensitivity = Sensitivity.MEDIUM,
            vibrationIntensity = VibrationIntensity.STRONG,
            isLargeTextEnabled = false,
            isOnboardingCompleted = false,
            thresholds = PersonalThresholds.DEFAULT
        )
    }
}
