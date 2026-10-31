package com.lost.watch.feature.monitoring

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lost.domain.session.TripSession
import com.lost.watch.integration.WalkingRouteGuide
import com.lost.watch.ui.map.MapRoutes
import com.lost.watch.ui.map.MapUiState
import com.lost.watch.ui.map.MapUiStateFlow
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class MonitoringViewModel @Inject constructor(
    session: TripSession,
    mapper: MonitoringUiStateMapper,
    walkingRoute: WalkingRouteGuide,
    mapStates: MapUiStateFlow
) : ViewModel() {
    /** Proyectar cientos de puntos en cada actualización no se hace en el hilo de la UI. */
    val uiState: StateFlow<MonitoringUiState> = session.snapshot
        .map(mapper::map)
        .flowOn(Dispatchers.Default)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), MonitoringUiState.IDLE)

    /** En rutina: la ruta de siempre; en lugar nuevo: la ruta a pie sugerida (si se pidió). */
    val map: StateFlow<MapUiState> = mapStates.of(
        combine(session.snapshot, walkingRoute.route) { snapshot, suggested ->
            MapRoutes(snapshot.routine?.path ?: suggested, snapshot.trip?.points.orEmpty())
        }
    ).stateIn(viewModelScope, SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS), MapUiState.Simple)

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}
