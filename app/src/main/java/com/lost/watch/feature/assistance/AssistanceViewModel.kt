package com.lost.watch.feature.assistance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.ObserveAuthorizedContactUseCase
import com.lost.watch.integration.ShareOutcomeStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class AssistanceViewModel @Inject constructor(
    private val session: TripSession,
    observeContact: ObserveAuthorizedContactUseCase,
    shareOutcome: ShareOutcomeStore,
    mapper: AssistanceUiStateMapper
) : ViewModel() {
    private val isBackOnRoute = MutableStateFlow(false)

    val uiState: StateFlow<AssistanceUiState> =
        combine(session.snapshot, observeContact(), shareOutcome.lastError, isBackOnRoute) {
                snapshot,
                contact,
                error,
                backOnRoute
            ->
            val mapped = mapper.map(snapshot, contact, error)
            if (backOnRoute && mapped == AssistanceUiState.Hidden) AssistanceUiState.BackOnRoute else mapped
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), AssistanceUiState.Hidden)

    init {
        session.transitions
            .filter { it.from == AssistanceState.Guiding && it.event == AssistanceEvent.RouteRecovered }
            .onEach { isBackOnRoute.value = true }
            .launchIn(viewModelScope)
    }

    fun onEvent(event: AssistanceUiEvent) {
        when (event) {
            AssistanceUiEvent.GoingElsewhere -> session.dispatch(AssistanceEvent.UserIsGoingElsewhere)

            AssistanceUiEvent.GuideMe, AssistanceUiEvent.KeepGuiding -> session.dispatch(
                AssistanceEvent.UserRequestedGuidance
            )

            AssistanceUiEvent.ImFine -> session.dispatch(AssistanceEvent.UserIsFine)

            AssistanceUiEvent.CancelSharing -> session.dispatch(AssistanceEvent.CountdownCancelled)

            AssistanceUiEvent.DismissBackOnRoute -> isBackOnRoute.value = false
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
