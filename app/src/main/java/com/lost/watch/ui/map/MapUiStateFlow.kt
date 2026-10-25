package com.lost.watch.ui.map

import com.lost.domain.error.LostResult
import com.lost.domain.port.SettingsReader
import com.lost.watch.ui.LostErrorToUiMessageMapper
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest

/**
 * Convierte "qué rutas mostrar" en el [MapUiState] de una pantalla. Si el usuario apagó
 * el mapa real no se pide nada a la red; si llega una ruta nueva se cancela la carga anterior.
 */
class MapUiStateFlow @Inject constructor(
    private val settings: SettingsReader,
    private val presenter: RouteMapPresenter,
    private val errors: LostErrorToUiMessageMapper
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun of(routes: Flow<MapRoutes>): Flow<MapUiState> {
        val isEnabled = settings.observeSettings().map { it.isRealMapEnabled }.distinctUntilChanged()
        return combine(isEnabled, routes.distinctUntilChanged()) { enabled, request -> enabled to request }
            .mapLatest { (enabled, request) -> if (enabled && !request.isEmpty) load(request) else MapUiState.Simple }
    }

    private suspend fun load(routes: MapRoutes): MapUiState =
        when (val result = presenter.present(routes.habitual, routes.today)) {
            is LostResult.Success -> MapUiState.Real(result.value)
            is LostResult.Failure -> MapUiState.Unavailable(errors.map(result.error))
        }
}
