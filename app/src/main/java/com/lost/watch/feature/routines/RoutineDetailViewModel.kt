package com.lost.watch.feature.routines

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ConfirmRoutineUseCase
import com.lost.domain.usecase.DeleteRoutineUseCase
import com.lost.domain.usecase.ObserveRoutinesUseCase
import com.lost.domain.usecase.RenameRoutineUseCase
import com.lost.watch.navigation.LostDestination
import com.lost.watch.ui.map.MapRoutes
import com.lost.watch.ui.map.MapUiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class RoutineDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeRoutines: ObserveRoutinesUseCase,
    private val actions: RoutineActions,
    mapper: RoutineItemMapper,
    mapStates: MapUiStateFlow
) : ViewModel() {
    private val routineId: String = checkNotNull(savedStateHandle[LostDestination.RoutineDetail.ARG_ROUTINE_ID])
    private val deletion = MutableStateFlow(DeletionStep.IDLE)

    private val routine = observeRoutines().map { routines -> routines.firstOrNull { it.id == routineId } }
    private val mapState = mapStates.of(routine.map { MapRoutes(it?.path.orEmpty(), emptyList()) })

    val uiState: StateFlow<RoutineDetailUiState> = combine(routine, deletion, mapState) { routine, step, map ->
        RoutineDetailUiState(
            item = routine?.let(mapper::map),
            path = routine?.path.orEmpty(),
            isDeleteArmed = step == DeletionStep.ARMED,
            isDeleted = step == DeletionStep.DONE,
            map = map
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        RoutineDetailUiState(null, emptyList(), false, false)
    )

    fun onEvent(event: RoutinesUiEvent) {
        viewModelScope.launch {
            when (event) {
                RoutinesUiEvent.Confirm -> actions.confirm(routineId)
                is RoutinesUiEvent.Rename -> actions.rename(routineId, event.newName)
                RoutinesUiEvent.DeleteTapped -> onDeleteTapped()
            }
        }
    }

    private suspend fun onDeleteTapped() {
        if (deletion.value == DeletionStep.ARMED) {
            actions.delete(routineId)
            deletion.value = DeletionStep.DONE
        } else {
            deletion.value = DeletionStep.ARMED
        }
    }

    private enum class DeletionStep { IDLE, ARMED, DONE }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
