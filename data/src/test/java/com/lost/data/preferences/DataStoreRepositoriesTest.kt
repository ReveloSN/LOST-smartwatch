package com.lost.data.preferences

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.lost.data.contacts.AuthorizedContactRepository
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.UserSettings
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir

class DataStoreRepositoriesTest {
    @TempDir
    lateinit var folder: File

    private fun TestScope.store(name: String) =
        PreferenceDataStoreFactory.create(scope = backgroundScope) { File(folder, "$name.preferences_pb") }

    @Test
    fun `sin datos guardados usa las preferencias por defecto`() = runTest {
        val repository = DataStoreSettingsRepository(store("settings"), SettingsPreferencesMapper())
        assertEquals(UserSettings.DEFAULT, repository.observeSettings().first())
    }

    @Test
    fun `los cambios de ajustes y umbrales se guardan`() = runTest {
        val repository = DataStoreSettingsRepository(store("settings"), SettingsPreferencesMapper())
        repository.update { it.copy(sensitivity = Sensitivity.HIGH, thresholds = PersonalThresholds(0.5, 0.8)) }
        val saved = repository.observeSettings().first()
        assertEquals(Sensitivity.HIGH, saved.sensitivity)
        assertEquals(PersonalThresholds(0.5, 0.8), saved.thresholds)
    }

    @Test
    fun `quitar permiso borra al contacto`() = runTest {
        val repository = AuthorizedContactRepository(InMemoryPreferencesStore())
        val laura = AuthorizedContact("c1", "Laura", "Hermana", "+57300", hasAcceptedInvitation = true)
        repository.save(laura)
        assertEquals(laura, repository.observeContact().first())
        repository.revoke()
        assertNull(repository.observeContact().first())
    }
}
