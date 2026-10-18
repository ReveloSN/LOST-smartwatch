package com.lost.domain.assistance

import com.lost.domain.assistance.AssistanceEvent.AcknowledgementElapsed
import com.lost.domain.assistance.AssistanceEvent.CountdownCancelled
import com.lost.domain.assistance.AssistanceEvent.CountdownTicked
import com.lost.domain.assistance.AssistanceEvent.DestinationReached
import com.lost.domain.assistance.AssistanceEvent.DetectionUpdated
import com.lost.domain.assistance.AssistanceEvent.ResponseTimedOut
import com.lost.domain.assistance.AssistanceEvent.RouteRecovered
import com.lost.domain.assistance.AssistanceEvent.UserIsFine
import com.lost.domain.assistance.AssistanceEvent.UserIsGoingElsewhere
import com.lost.domain.assistance.AssistanceEvent.UserRequestedGuidance
import com.lost.domain.assistance.AssistanceState.Acknowledged
import com.lost.domain.assistance.AssistanceState.AskForHelp
import com.lost.domain.assistance.AssistanceState.Calm
import com.lost.domain.assistance.AssistanceState.Guiding
import com.lost.domain.assistance.AssistanceState.LocationShared
import com.lost.domain.assistance.AssistanceState.SharingCountdown
import com.lost.domain.assistance.AssistanceState.SoftAsk
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionResult
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.DomainFixtures.reasons
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class AssistanceStateMachineTest {
    private val machine = AssistanceStateMachine()

    private fun detected(level: AssistanceLevel, voluntary: Boolean = false, contact: Boolean = true) =
        DetectionUpdated(DomainFixtures.result(level, voluntary), hasAuthorizedContact = contact)

    @Nested
    inner class DesdeCalma {
        @Test
        fun `nivel bajo sin desvio voluntario se queda en calma`() {
            assertEquals(Calm, machine.next(Calm, DetectionUpdated(DetectionResult.CALM, true)))
        }

        @Test
        fun `desvio voluntario pregunta suave`() {
            assertEquals(SoftAsk(reasons), machine.next(Calm, detected(AssistanceLevel.LOW, voluntary = true)))
        }

        @Test
        fun `nivel medio vibra y pregunta`() {
            assertEquals(AskForHelp(reasons), machine.next(Calm, detected(AssistanceLevel.MEDIUM)))
        }

        @Test
        fun `nivel alto con contacto inicia la cuenta regresiva`() {
            val next = machine.next(Calm, detected(AssistanceLevel.HIGH))
            assertEquals(SharingCountdown(AssistanceTimings.SHARING_COUNTDOWN_SECONDS), next)
        }

        @Test
        fun `nivel alto sin contacto pregunta en vez de compartir`() {
            assertEquals(AskForHelp(reasons), machine.next(Calm, detected(AssistanceLevel.HIGH, contact = false)))
        }
    }

    @Nested
    inner class DesdePreguntaSuave {
        private val softAsk = SoftAsk(reasons)

        @Test
        fun `voy a otro lado confirma y aprende`() {
            assertEquals(Acknowledged(UserFeedback.GOING_ELSEWHERE), machine.next(softAsk, UserIsGoingElsewhere))
        }

        @Test
        fun `guiame pasa a guiar`() {
            assertEquals(Guiding, machine.next(softAsk, UserRequestedGuidance))
        }

        @Test
        fun `si no responde LOST no insiste`() {
            assertEquals(Calm, machine.next(softAsk, ResponseTimedOut(hasAuthorizedContact = true)))
        }

        @Test
        fun `si empieza a dar vueltas escala a pedir ayuda`() {
            assertEquals(AskForHelp(reasons), machine.next(softAsk, detected(AssistanceLevel.MEDIUM)))
        }
    }

    @Nested
    inner class DesdePedirAyuda {
        private val asking = AskForHelp(reasons)

        @Test
        fun `si guiame pasa a guiar`() {
            assertEquals(Guiding, machine.next(asking, UserRequestedGuidance))
        }

        @Test
        fun `estoy bien confirma y sube el umbral`() {
            assertEquals(Acknowledged(UserFeedback.IM_FINE), machine.next(asking, UserIsFine))
        }

        @Test
        fun `30 segundos sin respuesta con contacto inicia la cuenta`() {
            val next = machine.next(asking, ResponseTimedOut(hasAuthorizedContact = true))
            assertEquals(SharingCountdown(AssistanceTimings.SHARING_COUNTDOWN_SECONDS), next)
        }

        @Test
        fun `30 segundos sin respuesta y sin contacto sigue preguntando`() {
            assertEquals(asking, machine.next(asking, ResponseTimedOut(hasAuthorizedContact = false)))
        }

        @Test
        fun `una lectura mas tranquila no borra la pregunta`() {
            assertEquals(asking, machine.next(asking, DetectionUpdated(DetectionResult.CALM, true)))
        }

        @Test
        fun `nivel alto escala a la cuenta regresiva`() {
            assertEquals(
                SharingCountdown(AssistanceTimings.SHARING_COUNTDOWN_SECONDS),
                machine.next(asking, detected(AssistanceLevel.HIGH))
            )
        }
    }

    @Nested
    inner class DesdeGuiaYCuenta {
        @Test
        fun `al recuperar la ruta vuelve a la calma`() {
            assertEquals(Calm, machine.next(Guiding, RouteRecovered))
        }

        @Test
        fun `guiando ignora otras lecturas`() {
            assertEquals(Guiding, machine.next(Guiding, detected(AssistanceLevel.HIGH)))
        }

        @Test
        fun `cada tic descuenta un segundo`() {
            assertEquals(SharingCountdown(4), machine.next(SharingCountdown(5), CountdownTicked))
        }

        @Test
        fun `al llegar a cero comparte la ubicacion`() {
            assertEquals(LocationShared, machine.next(SharingCountdown(1), CountdownTicked))
        }

        @Test
        fun `cancelar la cuenta vuelve a la calma`() {
            assertEquals(Calm, machine.next(SharingCountdown(3), CountdownCancelled))
        }

        @Test
        fun `tras compartir puede seguir guiando`() {
            assertEquals(Guiding, machine.next(LocationShared, UserRequestedGuidance))
        }

        @Test
        fun `el mensaje de entendido vuelve a inicio`() {
            assertEquals(Calm, machine.next(Acknowledged(UserFeedback.IM_FINE), AcknowledgementElapsed))
        }

        @Test
        fun `llegar al destino siempre termina en calma`() {
            listOf(Guiding, SharingCountdown(2), LocationShared, AskForHelp(reasons)).forEach {
                assertEquals(Calm, machine.next(it, DestinationReached))
            }
        }
    }
}
