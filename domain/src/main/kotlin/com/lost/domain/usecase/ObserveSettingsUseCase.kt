package com.lost.domain.usecase

import com.lost.domain.model.UserSettings
import com.lost.domain.port.SettingsReader
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/** Preferencias actuales del usuario. */
class ObserveSettingsUseCase @Inject constructor(private val reader: SettingsReader) {
    operator fun invoke(): Flow<UserSettings> = reader.observeSettings()
}
