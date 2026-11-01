package com.lost.watch.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.metrics.WeekActivity
import com.lost.domain.usecase.GetWeekActivityUseCase
import com.lost.domain.usecase.ObserveWeeklyInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class InsightsViewModel @Inject constructor(
    observeInsights: ObserveWeeklyInsightsUseCase,
    private val getWeekActivity: GetWeekActivityUseCase,
    mapper: InsightsUiStateMapper
) : ViewModel() {
    private val weekActivity = MutableStateFlow(WeekActivity.EMPTY)

    val uiState: StateFlow<InsightsUiState?> = combine(observeInsights(), weekActivity, mapper::map)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    init {
        onEvent(InsightsUiEvent.Refresh)
    }

    fun onEvent(event: InsightsUiEvent) {
        when (event) {
            InsightsUiEvent.Refresh -> viewModelScope.launch { weekActivity.value = getWeekActivity() }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
