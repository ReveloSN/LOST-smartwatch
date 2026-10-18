package com.lost.watch.feature.newplace

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.model.Destination
import com.lost.domain.usecase.ObserveDestinationsUseCase
import com.lost.domain.usecase.SaveDestinationUseCase
import com.lost.watch.integration.MonitoringController
import com.lost.watch.navigation.LostDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NewPlaceViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    observeDestinations: ObserveDestinationsUseCase,
    private val saveDestination: SaveDestinationUseCase,
    private val monitoringController: MonitoringController
) : ViewModel() {
    private val destinationId: String = checkNotNull(savedStateHandle[LostDestination.NewPlace.ARG_DESTINATION_ID])
    private val isStarted = MutableStateFlow(false)
    private val destination = MutableStateFlow<Destination?>(null)

    val uiState: StateFlow<NewPlaceUiState?> = combine(observeDestinations(), isStarted) { destinations, started ->
        destinations.firstOrNull { it.id == destinationId }?.also { destination.value = it }
            ?.let { NewPlaceUiState(it.name, it.kind, started) }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), null)

    fun onEvent(event: NewPlaceUiEvent) {
        val target = destination.value ?: return
        when (event) {
            NewPlaceUiEvent.Start -> {
                monitoringController.start(target)
                isStarted.value = true
                viewModelScope.launch { saveDestination(target) }
            }

            NewPlaceUiEvent.SaveDestination -> viewModelScope.launch { saveDestination(target) }

            NewPlaceUiEvent.Finish -> monitoringController.stop()
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
