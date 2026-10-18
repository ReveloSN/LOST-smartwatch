package com.lost.watch.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.data.demo.DemoMode
import com.lost.domain.model.UserSettings
import com.lost.domain.model.VibrationIntensity
import com.lost.domain.usecase.DeleteLearnedDataUseCase
import com.lost.domain.usecase.ObserveRoutinesUseCase
import com.lost.domain.usecase.ObserveSettingsUseCase
import com.lost.domain.usecase.UpdateSettingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    observeSettings: ObserveSettingsUseCase,
    observeRoutines: ObserveRoutinesUseCase,
    private val updateSettings: UpdateSettingsUseCase,
    private val deleteLearnedData: DeleteLearnedDataUseCase
) : ViewModel() {
    private val hasDeleted = MutableStateFlow(false)
    private var current = UserSettings.DEFAULT

    val uiState: StateFlow<SettingsUiState> = combine(observeSettings(), observeRoutines(), hasDeleted) {
            settings,
            routines,
            deleted
        ->
        current = settings
        SettingsUiState(
            sensitivity = settings.sensitivity,
            vibration = settings.vibrationIntensity,
            isLargeTextEnabled = settings.isLargeTextEnabled,
            habitualRoutineMinutes = routines.firstOrNull { it.isConfirmed }?.duration?.meanMinutes?.roundToInt(),
            isDemoAvailable = DemoMode.isEnabled,
            hasDeletedLearning = deleted
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), initialState())

    fun onEvent(event: SettingsUiEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsUiEvent.ChangeSensitivity -> updateSettings.changeSensitivity(event.sensitivity)

                SettingsUiEvent.ToggleVibration -> updateSettings.changeVibration(current.vibrationIntensity.toggled())

                SettingsUiEvent.ToggleLargeText -> updateSettings.changeLargeText(!current.isLargeTextEnabled)

                SettingsUiEvent.DeleteLearnedData -> {
                    deleteLearnedData()
                    hasDeleted.value = true
                }
            }
        }
    }

    private fun VibrationIntensity.toggled() =
        if (this == VibrationIntensity.STRONG) VibrationIntensity.SOFT else VibrationIntensity.STRONG

    private fun initialState() = SettingsUiState(
        sensitivity = UserSettings.DEFAULT.sensitivity,
        vibration = UserSettings.DEFAULT.vibrationIntensity,
        isLargeTextEnabled = false,
        habitualRoutineMinutes = null,
        isDemoAvailable = DemoMode.isEnabled,
        hasDeletedLearning = false
    )

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
