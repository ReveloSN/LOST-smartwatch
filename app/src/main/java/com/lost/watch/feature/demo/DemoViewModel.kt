package com.lost.watch.feature.demo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.data.demo.DemoRoutineLearning
import com.lost.data.demo.DemoScenario
import com.lost.data.demo.DemoScenarioSelector
import com.lost.data.demo.DemoWeekTrips
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class DemoViewModel @Inject constructor(
    selector: DemoScenarioSelector,
    private val player: DemoPlayer,
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
            is DemoUiEvent.Play -> player.play(event.scenario)
            DemoUiEvent.Stop -> player.stop()
            DemoUiEvent.LearnNextDay -> viewModelScope.launch { learnedDays.value = routineLearning.learnNextDay() }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
