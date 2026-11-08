package com.lost.domain.usecase

import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.UserSettings
import com.lost.domain.port.SettingsWriter
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class UpdateSettingsUseCaseTest {
    private var settings = UserSettings.DEFAULT
    private val writer = object : SettingsWriter {
        override suspend fun update(transform: (UserSettings) -> UserSettings) {
            settings = transform(settings)
        }
    }
    private val useCase = UpdateSettingsUseCase(writer)

    @Test
    fun `terminar la bienvenida la marca como completa`() = runTest {
        useCase.completeOnboarding()
        assertTrue(settings.isOnboardingCompleted)
    }

    @Test
    fun `empezar de nuevo vuelve a la bienvenida con los ajustes de fabrica`() = runTest {
        useCase.completeOnboarding()
        useCase.changeSensitivity(Sensitivity.HIGH)
        useCase.changeLargeText(isEnabled = true)
        settings = settings.copy(thresholds = PersonalThresholds(medium = 0.6, high = 0.95))

        useCase.startOver()

        assertFalse(settings.isOnboardingCompleted)
        assertEquals(UserSettings.DEFAULT, settings)
    }
}
