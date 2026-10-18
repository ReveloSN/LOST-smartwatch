package com.lost.watch.feature.assistance

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.error.LostError
import com.lost.domain.model.AuthorizedContact
import com.lost.watch.AppFixtures
import com.lost.watch.R
import com.lost.watch.ui.LostErrorToUiMessageMapper
import com.lost.watch.ui.StatusPresentation
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class AssistanceUiStateMapperTest {
    private val mapper = AssistanceUiStateMapper(LostErrorToUiMessageMapper())
    private val laura = AuthorizedContact("c", "Laura", "Hermana", "", true)

    private fun map(state: AssistanceState, level: AssistanceLevel = AssistanceLevel.MEDIUM, error: LostError? = null) =
        mapper.map(AppFixtures.monitoring(AppFixtures.detection(level), state), laura, error)

    @Test
    fun `en calma la capa de alerta no se ve`() {
        assertEquals(AssistanceUiState.Hidden, map(AssistanceState.Calm))
    }

    @Test
    fun `pedir ayuda nunca se muestra con el color de todo en orden`() {
        val state = map(AssistanceState.AskForHelp(AppFixtures.reasons), level = AssistanceLevel.LOW)
        assertEquals(StatusPresentation.DEVIATION, (state as AssistanceUiState.AskForHelp).status)
    }

    @Test
    fun `la cuenta regresiva lleva el nombre del contacto`() {
        val state = map(AssistanceState.SharingCountdown(3)) as AssistanceUiState.SharingCountdown
        assertEquals(3, state.secondsLeft)
        assertEquals("Laura", state.contactName)
    }

    @Test
    fun `si no se pudo avisar se muestra el mensaje de tres partes`() {
        val state = map(
            AssistanceState.LocationShared,
            error = LostError.NoConnection
        ) as AssistanceUiState.LocationShared
        assertEquals(R.string.error_connection_what, state.error?.whatHappened)
        assertEquals(R.string.error_connection_todo, state.error?.whatToDo)
    }

    @Test
    fun `entendido conserva la respuesta del usuario`() {
        assertEquals(
            AssistanceUiState.Acknowledged(UserFeedback.IM_FINE),
            map(AssistanceState.Acknowledged(UserFeedback.IM_FINE))
        )
    }
}
