package com.lost.watch.feature.assistance

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.lost.designsystem.theme.LostTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * En el reloj redondo el botón en forma de píldora de una alerta se ve completo sin desplazar: el borde
 * de abajo (sin contar sus extremos redondeados) queda dentro del círculo de la pantalla.
 * Las alertas con una segunda respuesta solo de texto (¿Necesitas ayuda?, ¿Vas a otro lado hoy?)
 * no entran: su área táctil sobresale a propósito del texto (decisión del 25 de octubre).
 */
class AlertsFitOnRoundScreenTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun cuentaRegresiva() = assertActionsInsideCircle {
        SharingCountdownAlert(AssistanceUiState.SharingCountdown(5, 5, "Laura")) {}
    }

    @Test
    fun ubicacionCompartida() = assertActionsInsideCircle {
        LocationSharedAlert(AssistanceUiState.LocationShared("Laura", null)) {}
    }

    private fun assertActionsInsideCircle(alert: @Composable () -> Unit) {
        compose.setContent { LostTheme { alert() } }
        val screen = compose.onRoot().fetchSemanticsNode().boundsInRoot
        val radius = screen.width / 2
        compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().forEach { node ->
            val button = node.boundsInRoot
            val corner = button.height / 2
            listOf(button.left + corner, button.right - corner).forEach { x ->
                val distance = (Offset(x, button.bottom) - screen.center).getDistance()
                assertTrue("botón en $button sale del círculo de radio $radius", distance <= radius)
            }
        }
    }
}
