package com.lost.domain.session

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.AssistanceTimings
import com.lost.domain.assistance.HapticPattern
import com.lost.domain.port.ContactReader
import com.lost.domain.port.HapticNotifier
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * El paso del tiempo en la escalada: repetir la vibración a los 15 s, 30 s sin respuesta,
 * la cuenta regresiva segundo a segundo y el "Entendido" que se cierra solo a los 3 s.
 * Cada cambio de estado cancela el temporizador anterior (`collectLatest`).
 */
class AssistanceTimers @Inject constructor(
    private val session: TripSession,
    private val contactReader: ContactReader,
    private val hapticNotifier: HapticNotifier
) {
    suspend fun run() {
        session.snapshot
            .map { it.assistance }
            .distinctUntilChangedBy(::timerKey)
            .collectLatest(::waitFor)
    }

    private suspend fun waitFor(state: AssistanceState) {
        when (state) {
            is AssistanceState.SoftAsk -> {
                delay(seconds(AssistanceTimings.NO_RESPONSE_TIMEOUT_SECONDS))
                session.dispatch(AssistanceEvent.ResponseTimedOut(hasContact()))
            }

            is AssistanceState.AskForHelp -> askAgainThenTimeOut()

            is AssistanceState.SharingCountdown -> {
                delay(seconds(1))
                session.dispatch(AssistanceEvent.CountdownTicked)
            }

            is AssistanceState.Acknowledged -> {
                delay(seconds(AssistanceTimings.ACKNOWLEDGEMENT_SECONDS))
                session.dispatch(AssistanceEvent.AcknowledgementElapsed)
            }

            else -> Unit
        }
    }

    private suspend fun askAgainThenTimeOut() {
        delay(seconds(AssistanceTimings.REPEAT_ASK_SECONDS))
        hapticNotifier.play(HapticPattern.DOUBLE)
        delay(seconds(AssistanceTimings.NO_RESPONSE_TIMEOUT_SECONDS - AssistanceTimings.REPEAT_ASK_SECONDS))
        session.dispatch(AssistanceEvent.ResponseTimedOut(hasContact()))
    }

    /** La cuenta regresiva cambia cada segundo; en los demás estados importa solo el tipo. */
    private fun timerKey(state: AssistanceState): Any =
        if (state is AssistanceState.SharingCountdown) state else state::class

    private suspend fun hasContact() = contactReader.observeContact().first() != null

    private fun seconds(value: Int) = TimeUnit.SECONDS.toMillis(value.toLong())
}
