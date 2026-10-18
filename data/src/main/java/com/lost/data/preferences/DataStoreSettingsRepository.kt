package com.lost.data.preferences

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import com.lost.data.di.SettingsStore
import com.lost.domain.model.UserSettings
import com.lost.domain.port.SettingsReader
import com.lost.domain.port.SettingsWriter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/** Preferencias del usuario guardadas con DataStore. */
class DataStoreSettingsRepository @Inject constructor(
    @SettingsStore private val dataStore: DataStore<Preferences>,
    private val mapper: SettingsPreferencesMapper
) : SettingsReader,
    SettingsWriter {
    override fun observeSettings(): Flow<UserSettings> = dataStore.data.map(mapper::toDomain).distinctUntilChanged()

    override suspend fun update(transform: (UserSettings) -> UserSettings) {
        dataStore.edit { preferences ->
            mapper.write(transform(mapper.toDomain(preferences)), preferences)
        }
    }
}
