package com.lost.domain.fakes

import com.lost.domain.model.UserSettings
import com.lost.domain.port.SettingsReader
import com.lost.domain.port.SettingsWriter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeSettingsRepository(initial: UserSettings = UserSettings.DEFAULT) :
    SettingsReader,
    SettingsWriter {
    val state = MutableStateFlow(initial)

    override fun observeSettings(): Flow<UserSettings> = state

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        state.value = transform(state.value)
    }
}
