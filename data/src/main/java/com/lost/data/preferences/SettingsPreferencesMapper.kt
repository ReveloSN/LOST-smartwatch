package com.lost.data.preferences

import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.UserSettings
import javax.inject.Inject

/** Lee y escribe [UserSettings] en DataStore. Lo que falte toma el valor por defecto. */
class SettingsPreferencesMapper @Inject constructor() {
    fun toDomain(preferences: Preferences): UserSettings {
        val defaults = UserSettings.DEFAULT
        return UserSettings(
            sensitivity = enumOrDefault(preferences[SettingsKeys.sensitivity], defaults.sensitivity),
            vibrationIntensity = enumOrDefault(preferences[SettingsKeys.vibration], defaults.vibrationIntensity),
            isLargeTextEnabled = preferences[SettingsKeys.largeText] ?: defaults.isLargeTextEnabled,
            isOnboardingCompleted = preferences[SettingsKeys.onboardingCompleted] ?: defaults.isOnboardingCompleted,
            thresholds = thresholdsOf(preferences),
            isRealMapEnabled = preferences[SettingsKeys.realMap] ?: defaults.isRealMapEnabled
        )
    }

    fun write(settings: UserSettings, preferences: MutablePreferences) {
        preferences[SettingsKeys.sensitivity] = settings.sensitivity.name
        preferences[SettingsKeys.vibration] = settings.vibrationIntensity.name
        preferences[SettingsKeys.largeText] = settings.isLargeTextEnabled
        preferences[SettingsKeys.onboardingCompleted] = settings.isOnboardingCompleted
        preferences[SettingsKeys.thresholdMedium] = settings.thresholds.medium
        preferences[SettingsKeys.thresholdHigh] = settings.thresholds.high
        preferences[SettingsKeys.realMap] = settings.isRealMapEnabled
    }

    private fun thresholdsOf(preferences: Preferences): PersonalThresholds {
        val medium = preferences[SettingsKeys.thresholdMedium] ?: return PersonalThresholds.DEFAULT
        val high = preferences[SettingsKeys.thresholdHigh] ?: return PersonalThresholds.DEFAULT
        return runCatching { PersonalThresholds(medium, high) }.getOrDefault(PersonalThresholds.DEFAULT)
    }

    private inline fun <reified T : Enum<T>> enumOrDefault(name: String?, default: T): T =
        enumValues<T>().firstOrNull { it.name == name } ?: default
}
