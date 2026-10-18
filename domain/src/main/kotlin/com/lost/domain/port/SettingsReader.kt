package com.lost.domain.port

import com.lost.domain.model.UserSettings
import kotlinx.coroutines.flow.Flow

/** Lectura de las preferencias del usuario. */
fun interface SettingsReader {
    fun observeSettings(): Flow<UserSettings>
}
