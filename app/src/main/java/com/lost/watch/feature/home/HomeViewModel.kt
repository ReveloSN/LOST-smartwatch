package com.lost.watch.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.data.demo.DemoMode
import com.lost.domain.session.TripSession
import com.lost.watch.integration.MonitoringController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class HomeViewModel @Inject constructor(
    session: TripSession,
    mapper: HomeUiStateMapper,
    private val monitoringController: MonitoringController
) : ViewModel() {
    val uiState: StateFlow<HomeUiState> = session.snapshot
        .map { mapper.map(it, DemoMode.isEnabled) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            HomeUiState.idle(DemoMode.isEnabled)
        )

    fun onEvent(event: HomeUiEvent) {
        when (event) {
            HomeUiEvent.StartTrip -> monitoringController.start()
            HomeUiEvent.StopTrip -> monitoringController.stop()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
