package com.lost.domain.port

import com.lost.domain.model.UserSettings

/** Escritura de las preferencias del usuario. */
interface SettingsWriter {
    suspend fun update(transform: (UserSettings) -> UserSettings)
}
