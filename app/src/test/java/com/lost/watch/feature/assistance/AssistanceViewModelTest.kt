package com.lost.watch.feature.assistance

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.port.ContactReader
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.ObserveAuthorizedContactUseCase
import com.lost.watch.AppFixtures
import com.lost.watch.MainDispatcherExtension
import com.lost.watch.integration.ShareOutcomeStore
import com.lost.watch.ui.LostErrorToUiMessageMapper
import kotlinx.coroutines.flow.flowOf
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MainDispatcherExtension::class)
class AssistanceViewModelTest {
    private val session = TripSession()
    private val viewModel = AssistanceViewModel(
        session = session,
        observeContact = ObserveAuthorizedContactUseCase(ContactReader { flowOf(null) }),
        shareOutcome = ShareOutcomeStore(),
        mapper = AssistanceUiStateMapper(LostErrorToUiMessageMapper())
    )

    private fun askForHelp() {
        session.start()
        session.dispatch(
            AssistanceEvent.DetectionUpdated(AppFixtures.detection(AssistanceLevel.MEDIUM, AppFixtures.reasons), true)
        )
    }

    @Test
    fun `si guiame pasa a guiar`() {
        askForHelp()
        viewModel.onEvent(AssistanceUiEvent.GuideMe)
        assertEquals(AssistanceState.Guiding, session.snapshot.value.assistance)
    }

    @Test
    fun `estoy bien confirma que LOST aprendera`() {
        askForHelp()
        viewModel.onEvent(AssistanceUiEvent.ImFine)
        assertEquals(AssistanceState.Acknowledged(UserFeedback.IM_FINE), session.snapshot.value.assistance)
    }
}
