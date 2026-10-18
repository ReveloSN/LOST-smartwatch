package com.lost.watch.feature.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.data.demo.DemoScenario
import com.lost.data.demo.DemoScenarioSelector
import com.lost.domain.usecase.ObserveDestinationsUseCase
import com.lost.watch.integration.MonitoringController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val selector: DemoScenarioSelector,
    private val observeDestinations: ObserveDestinationsUseCase,
    private val monitoringController: MonitoringController
) : ViewModel() {
    val uiState: StateFlow<DemoUiState> = selector.current
        .map { DemoUiState(DemoScenario.entries, it) }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            DemoUiState(DemoScenario.entries, null)
        )

    fun onEvent(event: DemoUiEvent) {
        when (event) {
            is DemoUiEvent.Play -> viewModelScope.launch { play(event.scenario) }

            DemoUiEvent.Stop -> {
                selector.stop()
                monitoringController.stop()
            }
        }
    }

    private suspend fun play(scenario: DemoScenario) {
        selector.stop()
        val destination = if (scenario == DemoScenario.NEW_PLACE) {
            observeDestinations().first().firstOrNull { it.id == HOSPITAL_DESTINATION_ID }
        } else {
            null
        }
        monitoringController.start(destination)
        selector.play(scenario)
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
        const val HOSPITAL_DESTINATION_ID = "demo-hospital"
    }
}
