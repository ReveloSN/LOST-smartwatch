package com.lost.watch.feature.errors

import androidx.lifecycle.ViewModel
import com.lost.domain.error.LostError
import com.lost.domain.port.DeviceStatusReader
import com.lost.watch.ui.LostErrorToUiMessageMapper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

@HiltViewModel
class ErrorViewModel @Inject constructor(
    private val deviceStatus: DeviceStatusReader,
    private val mapper: LostErrorToUiMessageMapper
) : ViewModel() {
    private val state = MutableStateFlow(ErrorUiState(emptyList()))
    val uiState: StateFlow<ErrorUiState> = state.asStateFlow()

    init {
        onEvent(ErrorUiEvent.Recheck)
    }

    fun onEvent(event: ErrorUiEvent) {
        when (event) {
            ErrorUiEvent.Recheck -> state.value = ErrorUiState(currentErrors().map(mapper::map))
        }
    }

    private fun currentErrors(): List<LostError> = buildList {
        if (!deviceStatus.hasLocationPermission()) add(LostError.LocationPermissionDenied)
        val battery = deviceStatus.batteryPercent()
        if (battery != null && battery <= LOW_BATTERY_PERCENT) add(LostError.BatteryLow(battery))
    }

    private companion object {
        const val LOW_BATTERY_PERCENT = 15
    }
}
