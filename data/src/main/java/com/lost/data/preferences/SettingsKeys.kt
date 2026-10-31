package com.lost.data.preferences

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

/** Claves de las preferencias del usuario en DataStore. */
internal object SettingsKeys {
    val sensitivity = stringPreferencesKey("sensitivity")
    val vibration = stringPreferencesKey("vibration_intensity")
    val largeText = booleanPreferencesKey("large_text")
    val onboardingCompleted = booleanPreferencesKey("onboarding_completed")
    val thresholdMedium = doublePreferencesKey("threshold_medium")
    val thresholdHigh = doublePreferencesKey("threshold_high")
    val realMap = booleanPreferencesKey("real_map")
}
