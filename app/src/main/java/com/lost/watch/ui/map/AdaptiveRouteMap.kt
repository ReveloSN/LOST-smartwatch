package com.lost.watch.ui.map

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.organisms.RealMapContent
import com.lost.designsystem.components.organisms.RealMapView
import com.lost.designsystem.components.organisms.RouteComparisonContent
import com.lost.designsystem.components.organisms.RouteComparisonMap
import com.lost.watch.R

/**
 * Mapa real si está listo; si no (apagado, cargando o sin internet), el mini-mapa vectorial
 * de siempre. Así la pantalla nunca queda vacía esperando a la red.
 */
@Composable
fun AdaptiveRouteMap(
    state: MapUiState,
    simple: RouteComparisonContent,
    sizing: AdaptiveMapSizing,
    showAttribution: Boolean = true
) {
    if (state is MapUiState.Real) {
        RealMapView(
            RealMapContent(
                tiles = state.picture.tiles,
                habitual = state.picture.habitual,
                today = state.picture.today,
                tone = simple.tone,
                description = simple.description,
                attribution = stringResource(R.string.map_attribution).takeIf { showAttribution }
            ),
            modifier = sizing.real
        )
    } else {
        RouteComparisonMap(simple, modifier = sizing.simple)
    }
}

/** Tamaño de cada variante: el mapa real puede ocupar toda la pantalla; el simple, un cuadro. */
data class AdaptiveMapSizing(val real: Modifier, val simple: Modifier)
