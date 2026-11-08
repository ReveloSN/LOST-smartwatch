package com.lost.watch.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.click
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performTouchInput
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import com.lost.designsystem.theme.LostTheme
import com.lost.watch.feature.assistance.SoftAskAlert
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Con una alerta encima, la pantalla de abajo no se toca ni la lee TalkBack. */
class AlertLayersTest {
    @get:Rule
    val compose = createComposeRule()

    private var taps by mutableIntStateOf(0)

    private fun showAlertOverScreen() = compose.setContent {
        LostTheme {
            AlertLayers(
                isAlertVisible = true,
                screen = { Button(onClick = { taps++ }, modifier = Modifier.fillMaxSize()) { Text(SCREEN) } },
                alert = { SoftAskAlert {} }
            )
        }
    }

    @Test
    fun laPantallaDeAbajoNoApareceParaTalkBack() {
        showAlertOverScreen()
        compose.onNodeWithText(SCREEN).assertDoesNotExist()
    }

    @Test
    fun unToqueEnUnaZonaVaciaDeLaAlertaNoLlegaALaPantallaDeAbajo() {
        showAlertOverScreen()
        // Esquina izquierda a media altura: dentro del círculo pero fuera de los botones de la alerta.
        compose.onRoot().performTouchInput { click(centerLeft.copy(x = width * EMPTY_X_FRACTION)) }
        compose.waitForIdle()
        assertEquals(0, taps)
    }

    private companion object {
        const val SCREEN = "pantalla de abajo"
        const val EMPTY_X_FRACTION = 0.08f
    }
}
