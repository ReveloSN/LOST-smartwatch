package com.lost.watch.feature.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.session.TripSession
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MonitoringViewModel @Inject constructor(session: TripSession, mapper: MonitoringUiStateMapper) : ViewModel() {
    val uiState: StateFlow<MonitoringUiState> = session.snapshot
        .map(mapper::map)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), MonitoringUiState.IDLE)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
