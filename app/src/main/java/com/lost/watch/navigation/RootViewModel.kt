package com.lost.watch.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.usecase.ObserveSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class RootViewModel @Inject constructor(observeSettings: ObserveSettingsUseCase) : ViewModel() {
    val uiState: StateFlow<RootUiState> = observeSettings()
        .map { RootUiState(isLoading = false, it.isOnboardingCompleted, it.isLargeTextEnabled) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, RootUiState.LOADING)
}
