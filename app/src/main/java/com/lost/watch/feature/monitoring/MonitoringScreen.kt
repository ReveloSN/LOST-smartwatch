package com.lost.watch.feature.monitoring

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.lost.designsystem.components.organisms.RouteComparisonMap
import com.lost.designsystem.components.templates.MapScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.R
import com.lost.watch.ui.ProjectedRoutes
import com.lost.watch.ui.StatusPresentation

/**
 * D.1–D.3 · El mapa (siempre vs. hoy) es la idea; arriba el estado y abajo un titular corto.
 * Si LOST nota algo, tocar el estado abre "¿Por qué te pregunto?".
 */
@Composable
fun MonitoringScreen(onOpenExplanation: () -> Unit, viewModel: MonitoringViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MonitoringContent(state) { event -> if (event == MonitoringUiEvent.OpenExplanation) onOpenExplanation() }
}

@Composable
private fun MonitoringContent(state: MonitoringUiState, onEvent: (MonitoringUiEvent) -> Unit) {
    val openExplanation = { onEvent(MonitoringUiEvent.OpenExplanation) }
    MapScreenTemplate(
        map = {
            RouteComparisonMap(
                RouteComparisonContent(
                    habitual = state.routes.habitual,
                    today = state.routes.today,
                    divergence = state.routes.divergence,
                    tone = state.status.tone,
                    description = stringResource(R.string.cd_route_comparison)
                ),
                modifier = Modifier.fillMaxHeight(ComponentTokens.MapScreen.MAP_HEIGHT_FRACTION).aspectRatio(1f)
            )
        },
        top = {
            LostChip(
                stringResource(state.status.label),
                icon = state.status.icon,
                tone = state.status.tone,
                onClick = openExplanation.takeIf { state.reasons.isNotEmpty() }
            )
        },
        bottom = { LostText(stringResource(state.headline.text), role = LostTextRole.TITLE) }
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
    LostTheme { MonitoringContent(state) {} }
}
