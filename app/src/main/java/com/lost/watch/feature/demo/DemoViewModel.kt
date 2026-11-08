package com.lost.watch.feature.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.data.demo.DemoRoutineLearning
import com.lost.data.demo.DemoScenario
import com.lost.data.demo.DemoScenarioSelector
import com.lost.data.demo.DemoWeekTrips
import com.lost.domain.usecase.ObserveDestinationsUseCase
import com.lost.watch.integration.MonitoringController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DemoViewModel @Inject constructor(
    private val selector: DemoScenarioSelector,
    private val observeDestinations: ObserveDestinationsUseCase,
    private val monitoringController: MonitoringController,
    private val routineLearning: DemoRoutineLearning
) : ViewModel() {
    private val learnedDays = MutableStateFlow(0)

    val uiState: StateFlow<DemoUiState> = combine(selector.current, learnedDays) { playing, days ->
        DemoUiState(DemoScenario.entries, playing, days, DemoWeekTrips.DAYS)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
        DemoUiState(DemoScenario.entries, null, daysToLearn = DemoWeekTrips.DAYS)
    )

    init {
        viewModelScope.launch { learnedDays.value = routineLearning.learnedDays() }
    }

    fun onEvent(event: DemoUiEvent) {
        when (event) {
            is DemoUiEvent.Play -> viewModelScope.launch { play(event.scenario) }

            DemoUiEvent.Stop -> {
                selector.stop()
                monitoringController.stop()
            }

            DemoUiEvent.LearnNextDay -> viewModelScope.launch { learnedDays.value = routineLearning.learnNextDay() }
        }
    }

    private suspend fun play(scenario: DemoScenario) {
        selector.stop()
        // Los escenarios de la ruta de siempre se comparan con ella: si aún no la aprendió, aprende la semana.
        if (scenario != DemoScenario.NEW_PLACE) routineLearning.learnWholeWeek()
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
