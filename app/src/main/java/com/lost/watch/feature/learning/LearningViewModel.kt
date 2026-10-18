package com.lost.watch.feature.learning

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ConfirmRoutineUseCase
import com.lost.domain.usecase.ObserveRoutinesUseCase
import com.lost.watch.navigation.LostDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class LearningViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeRoutines: ObserveRoutinesUseCase,
    private val confirmRoutine: ConfirmRoutineUseCase
) : ViewModel() {
    private val routineId: String = checkNotNull(savedStateHandle[LostDestination.Learning.ARG_ROUTINE_ID])

    val uiState: StateFlow<LearningUiState?> = observeRoutines()
        .map { routines ->
            routines.firstOrNull { it.id == routineId }?.let {
                LearningUiState(it.timesObserved, REQUIRED_REPETITIONS, it.destinationKind)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    fun onEvent(event: LearningUiEvent) {
        when (event) {
            LearningUiEvent.ConfirmNow -> viewModelScope.launch { confirmRoutine(routineId) }
        }
    }

    private companion object {
        const val REQUIRED_REPETITIONS = 5
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
