package com.lost.domain.model

/** Preferencias del usuario (pantalla Ajustes). */
data class UserSettings(
    val sensitivity: Sensitivity,
    val vibrationIntensity: VibrationIntensity,
    val isLargeTextEnabled: Boolean,
    val isOnboardingCompleted: Boolean,
    val thresholds: PersonalThresholds,
    /** Mapa real de OpenStreetMap (usa internet para las teselas). Apagado → mapa simple sin calles. */
    val isRealMapEnabled: Boolean = true
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
