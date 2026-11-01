package com.lost.domain.session

import app.cash.turbine.test
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.fakes.DomainFixtures
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class TripSessionTest {
    private val session = TripSession()

    @Test
    fun `empezar y terminar cambian el estado de monitoreo`() {
        session.start()
        assertTrue(session.snapshot.value.isMonitoring)
        session.stop()
        assertFalse(session.snapshot.value.isMonitoring)
    }

    @Test
    fun `cada evento pasa por la maquina de estados y publica la transicion`() = runTest {
        session.transitions.test {
            val detected = AssistanceEvent.DetectionUpdated(DomainFixtures.result(AssistanceLevel.MEDIUM), true)
            session.dispatch(detected)
            val transition = awaitItem()
            assertEquals(AssistanceState.Calm, transition.from)
            assertTrue(transition.to is AssistanceState.AskForHelp)
            assertTrue(session.snapshot.value.assistance is AssistanceState.AskForHelp)

            session.dispatch(AssistanceEvent.UserRequestedGuidance)
            assertEquals(AssistanceState.Guiding, awaitItem().to)
        }
    }

    @Test
    fun `voy a otro lado se recuerda hasta el siguiente recorrido`() {
        session.start()
        session.dispatch(AssistanceEvent.DetectionUpdated(DomainFixtures.result(AssistanceLevel.LOW, true), true))
        session.dispatch(AssistanceEvent.UserIsGoingElsewhere)
        session.dispatch(AssistanceEvent.AcknowledgementElapsed)
        assertTrue(session.snapshot.value.isGoingElsewhere)
        session.start()
        assertFalse(session.snapshot.value.isGoingElsewhere)
    }

    @Test
    fun `voy a otro lado sin una pregunta abierta no cambia nada`() {
        session.start()
        session.dispatch(AssistanceEvent.UserIsGoingElsewhere)
        assertFalse(session.snapshot.value.isGoingElsewhere)
    }
}
