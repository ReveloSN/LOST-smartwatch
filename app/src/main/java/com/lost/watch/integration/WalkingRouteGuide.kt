package com.lost.watch.integration

import com.lost.data.di.ApplicationScope
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.SettingsReader
import com.lost.domain.session.TripSession
import com.lost.domain.usecase.FetchWalkingRouteUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

/**
 * Ruta a pie sugerida en un lugar nuevo. Se pide una sola vez, cuando el usuario elige el destino
 * y con el mapa real encendido: viajan solo el primer punto del recorrido y el destino.
 */
@Singleton
class WalkingRouteGuide @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val session: TripSession,
    private val settings: SettingsReader,
    private val fetchWalkingRoute: FetchWalkingRouteUseCase
) {
    private val suggested = MutableStateFlow<List<GeoPoint>>(emptyList())
    private var job: Job? = null

    val route: StateFlow<List<GeoPoint>> = suggested.asStateFlow()

    fun requestFor(destination: Destination) {
        clear()
        job = scope.launch {
            if (!settings.observeSettings().first().isRealMapEnabled) return@launch
            val origin = session.snapshot.mapNotNull { it.currentLocation }.first()
            fetchWalkingRoute(origin, destination.location).valueOrNull()?.let { suggested.value = it }
        }
    }

    fun clear() {
        job?.cancel()
        suggested.value = emptyList()
    }
}
