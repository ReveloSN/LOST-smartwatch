package com.lost.watch.feature.routines

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ObserveRoutinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RoutinesViewModel @Inject constructor(observeRoutines: ObserveRoutinesUseCase, mapper: RoutineItemMapper) :
    ViewModel() {
    val uiState: StateFlow<RoutinesUiState> = observeRoutines()
        .map { routines -> RoutinesUiState(isLoading = false, items = routines.map(mapper::map)) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            RoutinesUiState(true, emptyList())
        )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
