package com.lost.watch.feature.assistance

import androidx.compose.runtime.Composable
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.AppScaffold
import com.lost.designsystem.theme.LostTheme
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.Tone
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

/**
 * En el reloj redondo el botón en forma de píldora de una alerta se ve completo sin desplazar: el borde
 * de abajo (sin contar sus extremos redondeados) queda dentro del círculo de la pantalla, con un margen.
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

    @Test
    fun preguntaDeAyudaMuestraLasDosRespuestas() = assertActionsVisibleWithoutScrolling {
        val reasons = listOf(DeviationReason.LeftUsualRoute(Meters(150.0)))
        AskForHelpAlert(AssistanceUiState.AskForHelp(StatusPresentation.DEVIATION, Tone.ATTENTIVE, reasons)) {}
    }

    @Test
    fun preguntaSuaveMuestraLasDosRespuestas() = assertActionsVisibleWithoutScrolling { SoftAskAlert {} }

    /** Para las alertas con respuesta solo de texto: cada respuesta se ve entera sin desplazar. */
    private fun assertActionsVisibleWithoutScrolling(alert: @Composable () -> Unit) {
        compose.setContent { LostTheme { AppScaffold { alert() } } }
        val actions = compose.onAllNodes(hasClickAction()).fetchSemanticsNodes()
        assertTrue("se esperaban dos respuestas", actions.size == 2)
        actions.forEach { node ->
            val visible = node.boundsInRoot
            assertTrue(
                "respuesta recortada: ${visible.height} de ${node.size.height}",
                visible.height >= node.size.height
            )
        }
    }

    private fun assertActionsInsideCircle(alert: @Composable () -> Unit) {
        // Como en LostApp: AppScaffold reserva arriba el espacio de la hora.
        compose.setContent { LostTheme { AppScaffold { alert() } } }
        val screen = compose.onRoot().fetchSemanticsNode().boundsInRoot
        // Un margen visible hasta el borde: si el botón llega justo al círculo se ve cortado.
        val radius = screen.width / 2 - with(compose.density) { EDGE_MARGIN.toPx() }
        compose.onAllNodes(hasClickAction()).fetchSemanticsNodes().forEach { node ->
            val button = node.boundsInRoot
            // Si el contenido no cabe, la columna se desplaza y recorta el botón: lo visible es más bajo que el botón.
            assertTrue("botón recortado: ${button.height} de ${node.size.height}", button.height >= node.size.height)
            val corner = button.height / 2
            listOf(button.left + corner, button.right - corner).forEach { x ->
                val distance = (Offset(x, button.bottom) - screen.center).getDistance()
                assertTrue("botón en $button sale del círculo de radio $radius", distance <= radius)
            }
        }
    }

    private companion object {
        val EDGE_MARGIN = 4.dp
    }
}
