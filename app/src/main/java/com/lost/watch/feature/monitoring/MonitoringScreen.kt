package com.lost.watch.feature.monitoring

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostChip
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.organisms.RouteComparisonContent
import com.lost.designsystem.components.templates.MapScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.R
import com.lost.watch.ui.ProjectedRoutes
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.map.AdaptiveMapSizing
import com.lost.watch.ui.map.AdaptiveRouteMap
import com.lost.watch.ui.map.MapUiState

/**
 * D.1–D.3 · El mapa (siempre vs. hoy) es la idea; arriba el estado y abajo un titular corto.
 * Con internet es el mapa real de OpenStreetMap (atribución bajo el estado, donde cabe en una línea);
 * sin él, el mini-mapa simple y una línea que dice qué hace LOST.
 * Si LOST nota algo, tocar el estado abre "¿Por qué te pregunto?".
 */
@Composable
fun MonitoringScreen(onOpenExplanation: () -> Unit, viewModel: MonitoringViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val map by viewModel.map.collectAsStateWithLifecycle()
    MonitoringContent(state, map) { event -> if (event == MonitoringUiEvent.OpenExplanation) onOpenExplanation() }
}

@Composable
private fun MonitoringContent(state: MonitoringUiState, map: MapUiState, onEvent: (MonitoringUiEvent) -> Unit) {
    val openExplanation = { onEvent(MonitoringUiEvent.OpenExplanation) }
    MapScreenTemplate(
        map = { MonitoringMap(state, map) },
        top = { MonitoringStatus(state, map, openExplanation) },
        bottom = {
            LostText(stringResource(state.headline.text), role = LostTextRole.TITLE)
            (map as? MapUiState.Unavailable)?.let {
                LostText(
                    stringResource(it.message.whatToDo),
                    role = LostTextRole.CAPTION,
                    color = SemanticTokens.Text.secondary
                )
            }
        }
    )
}

/** Arriba: el estado (tocarlo explica el porqué) y, con mapa real, la atribución de OpenStreetMap. */
@Composable
private fun MonitoringStatus(state: MonitoringUiState, map: MapUiState, openExplanation: () -> Unit) {
    LostChip(
        stringResource(state.status.label),
        icon = state.status.icon,
        tone = state.status.tone,
        onClick = openExplanation.takeIf { state.reasons.isNotEmpty() }
    )
    if (map is MapUiState.Real) {
        LostText(
            stringResource(R.string.map_attribution),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}

@Composable
private fun MonitoringMap(state: MonitoringUiState, map: MapUiState) {
    AdaptiveRouteMap(
        state = map,
        simple = RouteComparisonContent(
            habitual = state.routes.habitual,
            today = state.routes.today,
            divergence = state.routes.divergence,
            tone = state.status.tone,
            description = stringResource(R.string.cd_route_comparison)
        ),
        sizing = AdaptiveMapSizing(
            real = Modifier.fillMaxSize(),
            simple = Modifier.fillMaxHeight(ComponentTokens.MapScreen.MAP_HEIGHT_FRACTION).aspectRatio(1f)
        ),
        showAttribution = false
    )
}

@LostDevicePreviews
@Composable
private fun MonitoringPreview() {
    val state = MonitoringUiState(
        headline = MonitoringHeadline.NOT_SIMILAR,
        status = StatusPresentation.DEVIATION,
        routes = ProjectedRoutes(emptyList(), emptyList(), null),
        reasons = listOf(DeviationReason.LeftUsualRoute(Meters(150.0)), DeviationReason.RepeatedArea(3))
    )
    LostTheme { MonitoringContent(state, MapUiState.Simple) {} }
}
