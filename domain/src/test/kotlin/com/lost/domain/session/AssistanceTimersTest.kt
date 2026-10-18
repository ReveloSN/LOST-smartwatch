package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.HapticPattern
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.port.ContactReader
import com.lost.domain.port.HapticNotifier
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssistanceTimersTest {
    private val session = TripSession()
    private val haptics = mutableListOf<HapticPattern>()
    private val timers =
        AssistanceTimers(
            session,
            ContactReader { flowOf(DomainFixtures.laura) },
            HapticNotifier {
                haptics +=
                    it
            }
        )

    private fun TestScope.startTimers() {
        backgroundScope.launch { timers.run() }
        runCurrent()
    }

    @Test
    fun `sin respuesta repite la vibracion a los 15 s y a los 30 s inicia la cuenta`() = runTest {
        startTimers()
        session.dispatch(AssistanceEvent.DetectionUpdated(DomainFixtures.result(AssistanceLevel.MEDIUM), true))
        advanceTimeBy(15_001)
        assertEquals(listOf(HapticPattern.DOUBLE), haptics)
        advanceTimeBy(15_000)
        assertEquals(AssistanceState.SharingCountdown(5), session.snapshot.value.assistance)
    }

    @Test
    fun `la cuenta regresiva baja cada segundo hasta compartir`() = runTest {
        startTimers()
        session.dispatch(AssistanceEvent.DetectionUpdated(DomainFixtures.result(AssistanceLevel.HIGH), true))
        advanceTimeBy(2_001)
        assertEquals(AssistanceState.SharingCountdown(3), session.snapshot.value.assistance)
        advanceTimeBy(3_000)
        assertEquals(AssistanceState.LocationShared, session.snapshot.value.assistance)
    }

    @Test
    fun `entendido vuelve a la calma a los 3 s`() = runTest {
        startTimers()
        session.dispatch(AssistanceEvent.DetectionUpdated(DomainFixtures.result(AssistanceLevel.MEDIUM), true))
        session.dispatch(AssistanceEvent.UserIsFine)
        assertEquals(AssistanceState.Acknowledged(UserFeedback.IM_FINE), session.snapshot.value.assistance)
        advanceTimeBy(3_001)
        assertEquals(AssistanceState.Calm, session.snapshot.value.assistance)
    }
}
