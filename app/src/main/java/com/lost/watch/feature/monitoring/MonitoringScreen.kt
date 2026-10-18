package com.lost.watch.feature.monitoring

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostChip
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.molecules.ReasonRow
import com.lost.designsystem.components.organisms.ProbabilityArc
import com.lost.designsystem.components.organisms.RouteComparisonContent
import com.lost.designsystem.components.organisms.RouteComparisonMap
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.R
import com.lost.watch.ui.ProjectedRoutes
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.reasonContent

private const val MAP_WIDTH_FRACTION = 0.85f

/** D.1–D.3 · Dos líneas (siempre vs. hoy), el nivel en el arco y solo las señales que se activaron. */
@Composable
fun MonitoringScreen(onOpenExplanation: () -> Unit, viewModel: MonitoringViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    MonitoringContent(state) { event -> if (event == MonitoringUiEvent.OpenExplanation) onOpenExplanation() }
}

@Composable
private fun MonitoringContent(state: MonitoringUiState, onEvent: (MonitoringUiEvent) -> Unit) {
    WatchScreenTemplate(ring = { ProbabilityArc(state.status.tone, state.probabilityFraction) }) {
        LostChip(stringResource(state.status.label), icon = state.status.icon, tone = state.status.tone)
        RouteComparisonMap(
            RouteComparisonContent(
                habitual = state.routes.habitual,
                today = state.routes.today,
                divergence = state.routes.divergence,
                tone = state.status.tone,
                description = stringResource(R.string.cd_route_comparison)
            ),
            modifier = Modifier.fillMaxWidth(MAP_WIDTH_FRACTION).aspectRatio(1f)
        )
        LostText(stringResource(state.headline.text), role = LostTextRole.TITLE)
        if (state.reasons.isNotEmpty()) {
            LostText(
                stringResource(R.string.monitoring_what_i_notice),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
            state.reasons.forEach { ReasonRow(reasonContent(it), tone = state.status.tone) }
            LostSecondaryButton(
                label = stringResource(R.string.explanation_title),
                onClick = { onEvent(MonitoringUiEvent.OpenExplanation) },
                style = SecondaryButtonStyle.TEXT_ONLY
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun MonitoringPreview() {
    val state = MonitoringUiState(
        headline = MonitoringHeadline.NOT_SIMILAR,
        status = StatusPresentation.DEVIATION,
        probabilityFraction = 0.6f,
        routes = ProjectedRoutes(emptyList(), emptyList(), null),
        reasons = listOf(DeviationReason.LeftUsualRoute(Meters(150.0)), DeviationReason.RepeatedArea(3))
    )
    LostTheme { MonitoringContent(state) {} }
}
