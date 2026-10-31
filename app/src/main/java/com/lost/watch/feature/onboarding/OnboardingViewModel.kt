package com.lost.watch.feature.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ObserveAuthorizedContactUseCase
import com.lost.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    observeContact: ObserveAuthorizedContactUseCase,
    private val updateSettings: UpdateSettingsUseCase
) : ViewModel() {
    val uiState: StateFlow<OnboardingUiState> = observeContact()
        .map { OnboardingUiState(it?.name) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), OnboardingUiState(null))

    fun onEvent(event: OnboardingUiEvent) {
        when (event) {
            OnboardingUiEvent.Finish -> viewModelScope.launch { updateSettings.completeOnboarding() }
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
