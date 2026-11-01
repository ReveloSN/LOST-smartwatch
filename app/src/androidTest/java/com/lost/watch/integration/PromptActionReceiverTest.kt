package com.lost.watch.integration

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionResult
import com.lost.domain.model.units.Probability
import com.lost.domain.session.TripSession
import dagger.hilt.android.EntryPointAccessors
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Los botones de la notificación llegan a la sesión real: se envía al receptor la misma difusión que dispara el
 * botón (sin depender de tocar la tarjeta) y se mira el estado de la asistencia.
 */
@RunWith(AndroidJUnit4::class)
class PromptActionReceiverTest {
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val session: TripSession =
        EntryPointAccessors.fromApplication(context, PromptActionReceiver.SessionEntryPoint::class.java).session()

    @Before
    fun askForHelp() {
        session.start()
        val medium =
            DetectionResult(
                Probability(0.6),
                AssistanceLevel.MEDIUM,
                emptyList(),
                false,
                DetectionModeKind.ROUTINE,
                null,
                0.3
            )
        session.dispatch(AssistanceEvent.DetectionUpdated(medium, true))
    }

    @After
    fun stop() = session.stop()

    @Test
    fun estoyBienDesdeLaNotificacionLlegaALaSesion() {
        send(PromptAction.IM_FINE)
        assertEquals(AssistanceState.Acknowledged(UserFeedback.IM_FINE), session.snapshot.value.assistance)
    }

    @Test
    fun unaRespuestaDesconocidaNoCambiaNada() {
        PromptActionReceiver().onReceive(context, Intent().putExtra(PromptAction.EXTRA, "BORRAR_TODO"))
        assertEquals(true, session.snapshot.value.assistance is AssistanceState.AskForHelp)
    }

    /**
     * Envía de verdad la difusión que dispara el botón (el mismo intent explícito que arma la notificación), así
     * también se prueba que el receptor está registrado, y espera a que la sesión cambie.
     */
    private fun send(action: PromptAction) {
        val intent = Intent(context, PromptActionReceiver::class.java).putExtra(PromptAction.EXTRA, action.name)
        context.sendBroadcast(intent)
        val deadline = System.currentTimeMillis() + TIMEOUT_MILLIS
        while (session.snapshot.value.assistance is AssistanceState.AskForHelp &&
            System.currentTimeMillis() < deadline
        ) {
            Thread.sleep(POLL_MILLIS)
        }
    }

    private companion object {
        const val TIMEOUT_MILLIS = 3_000L
        const val POLL_MILLIS = 50L
    }
}
