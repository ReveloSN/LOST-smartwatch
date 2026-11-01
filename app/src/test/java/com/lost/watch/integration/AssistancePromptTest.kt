package com.lost.watch.integration

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.session.TripSession
import com.lost.watch.AppFixtures
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AssistancePromptTest {
    private val policy = AssistancePromptPolicy()

    @Test
    fun `con la app oculta pregunta por notificacion`() {
        assertEquals(AssistancePrompt.ASK_FOR_HELP, policy.promptFor(AssistanceState.AskForHelp(emptyList()), false))
        assertEquals(AssistancePrompt.SOFT_ASK, policy.promptFor(AssistanceState.SoftAsk(emptyList()), false))
    }

    @Test
    fun `con la app a la vista o sin pregunta no notifica`() {
        assertNull(policy.promptFor(AssistanceState.AskForHelp(emptyList()), true))
        assertNull(policy.promptFor(AssistanceState.Guiding, false))
        assertNull(policy.promptFor(AssistanceState.Calm, false))
    }

    @Test
    fun `las respuestas de la notificacion son las de la alerta`() {
        assertEquals(AssistanceEvent.UserRequestedGuidance, AssistancePrompt.ASK_FOR_HELP.primary.event)
        assertEquals(AssistanceEvent.UserIsFine, AssistancePrompt.ASK_FOR_HELP.secondary.event)
        assertEquals(AssistanceEvent.UserIsGoingElsewhere, AssistancePrompt.SOFT_ASK.primary.event)
    }

    @Test
    fun `la notificacion aparece al preguntar y se retira al responder o al abrir la app`() = runTest {
        val session = TripSession()
        val visibility = AppVisibility()
        val shown = mutableListOf<AssistancePrompt?>()
        val notifications = object : PromptNotifications {
            override fun show(prompt: AssistancePrompt) {
                shown += prompt
            }

            override fun cancel() {
                shown += null
            }
        }
        backgroundScope.launch { AssistancePromptEffect(session, visibility, policy, notifications).run() }
        runCurrent()
        session.dispatch(AssistanceEvent.DetectionUpdated(AppFixtures.detection(AssistanceLevel.MEDIUM), true))
        runCurrent()
        visibility.update(isVisible = true)
        runCurrent()
        assertEquals(listOf(null, AssistancePrompt.ASK_FOR_HELP, null), shown)
    }
}
