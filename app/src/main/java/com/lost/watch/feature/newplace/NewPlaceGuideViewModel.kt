package com.lost.watch.feature.newplace

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.session.ArrowBearing
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.ObserveCompassHeadingUseCase
import com.lost.domain.usecase.SaveDestinationUseCase
import com.lost.watch.integration.MonitoringController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class NewPlaceGuideViewModel @Inject constructor(
    private val session: TripSession,
    private val saveDestination: SaveDestinationUseCase,
    private val monitoringController: MonitoringController,
    observeHeading: ObserveCompassHeadingUseCase
) : ViewModel() {
    private val heading = observeHeading(session.snapshot.map { !it.hasArrived && it.guidance != null })

    val uiState: StateFlow<NewPlaceGuideUiState> = combine(session.snapshot, heading) { snapshot, compass ->
        val bearing = snapshot.guidance?.bearing?.value ?: 0.0
        NewPlaceGuideUiState(
            destinationName = snapshot.destination?.name,
            distanceMeters = snapshot.guidance?.distance?.value,
            bearingDegrees = bearing.toFloat(),
            hasArrived = snapshot.hasArrived,
            arrowDegrees = ArrowBearing.onScreen(bearing, compass).toFloat()
        )
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            NewPlaceGuideUiState(null, null, 0f, false)
        )

    fun onEvent(event: NewPlaceUiEvent) {
        when (event) {
            NewPlaceUiEvent.SaveDestination -> session.snapshot.value.destination?.let {
                viewModelScope.launch { saveDestination(it) }
            }

            NewPlaceUiEvent.Finish -> monitoringController.stop()

            NewPlaceUiEvent.Start -> Unit
        }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
