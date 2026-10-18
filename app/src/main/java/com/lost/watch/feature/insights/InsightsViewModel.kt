package com.lost.watch.feature.insights

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.GetWeekTripDaysUseCase
import com.lost.domain.usecase.ObserveWeeklyInsightsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.DayOfWeek
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
    private val getWeekTripDays: GetWeekTripDaysUseCase,
    mapper: InsightsUiStateMapper
) : ViewModel() {
    private val tripDays = MutableStateFlow<Set<DayOfWeek>>(emptySet())

    val uiState: StateFlow<InsightsUiState?> = combine(observeInsights(), tripDays, mapper::map)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    init {
        onEvent(InsightsUiEvent.Refresh)
    }

    fun onEvent(event: InsightsUiEvent) {
        when (event) {
            InsightsUiEvent.Refresh -> viewModelScope.launch { tripDays.value = getWeekTripDays() }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
