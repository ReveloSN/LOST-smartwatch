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

    private var isBatteryAcknowledged = false

    init {
        onEvent(ErrorUiEvent.Recheck)
    }

    fun onEvent(event: ErrorUiEvent) {
        if (event == ErrorUiEvent.Acknowledge) isBatteryAcknowledged = true
        state.value = ErrorUiState(currentNotices())
    }

    private fun currentNotices(): List<ErrorNotice> = buildList {
        if (!deviceStatus.hasLocationPermission()) {
            add(ErrorNotice(mapper.map(LostError.LocationPermissionDenied), ErrorRemedy.REQUEST_LOCATION))
        }
        val battery = deviceStatus.batteryPercent()
        if (!isBatteryAcknowledged && battery != null && battery <= LOW_BATTERY_PERCENT) {
            add(ErrorNotice(mapper.map(LostError.BatteryLow(battery)), ErrorRemedy.ACKNOWLEDGE))
        }
    }

    private companion object {
        const val LOW_BATTERY_PERCENT = 15
    }
}
