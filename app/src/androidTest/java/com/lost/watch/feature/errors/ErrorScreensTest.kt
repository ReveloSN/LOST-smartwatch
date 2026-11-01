package com.lost.watch.feature.errors

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.unit.Density
import androidx.test.platform.app.InstrumentationRegistry
import androidx.wear.compose.material3.AppScaffold
import com.lost.designsystem.theme.LostTheme
import com.lost.domain.error.LostError
import com.lost.watch.ui.LostErrorToUiMessageMapper
import com.lost.watch.ui.UiMessage
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test

/**
 * 10 · Cada error conocido se ve en el reloj en lenguaje del usuario: "qué pasó" a la vista al abrir y
 * "qué hacer" en la pantalla (desplazando con la corona si hace falta), también con Texto grande (+30 %).
 * Ningún texto muestra el nombre interno del error.
 */
class ErrorScreensTest {
    @get:Rule
    val compose = createComposeRule()

    private val mapper = LostErrorToUiMessageMapper()
    private val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources

    private val errors = listOf(
        LostError.LocationUnavailable,
        LostError.NoConnection,
        LostError.BatteryLow(BATTERY_PERCENT),
        LostError.NoAuthorizedContact,
        LostError.LocationPermissionDenied,
        LostError.NoRoutinesYet,
        LostError.StorageFailure("SQLiteDiskIOException"),
        LostError.MapUnavailable,
        LostError.SearchUnavailable
    )

    @Test
    fun cadaErrorSeVeConQuePasoYQueHacer() = assertEveryErrorShown(fontScale = 1f)

    @Test
    fun cadaErrorSeVeConTextoGrande() = assertEveryErrorShown(fontScale = LARGE_TEXT_SCALE)

    private fun assertEveryErrorShown(fontScale: Float) {
        var current by mutableStateOf(mapper.map(errors.first()))
        compose.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale)) {
                LostTheme { AppScaffold { ErrorScreen(current, iconFor(current)) } }
            }
        }
        errors.map(mapper::map).forEach { message ->
            current = message
            compose.waitForIdle()
            assertShown(message)
        }
    }

    private fun assertShown(message: UiMessage) {
        val whatHappened = message.whatHappenedArgument
            ?.let { resources.getString(message.whatHappened, it) }
            ?: resources.getString(message.whatHappened)
        val whatToDo = resources.getString(message.whatToDo)
        compose.onNodeWithText(whatHappened).assertIsDisplayed()
        compose.onNodeWithText(whatToDo).assertExists()
        listOf(whatHappened, whatToDo).forEach { text ->
            assertFalse("código interno a la vista: $text", text.contains("Exception") || text.contains("Error"))
        }
    }

    private companion object {
        const val BATTERY_PERCENT = 12
        const val LARGE_TEXT_SCALE = 1.3f
    }
}
