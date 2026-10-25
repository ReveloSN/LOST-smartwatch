package com.lost.watch.feature.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostChip
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.organisms.BreathingStatusRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import com.lost.watch.feature.errors.ErrorScreen
import com.lost.watch.feature.errors.ErrorViewModel
import com.lost.watch.feature.errors.iconFor
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.formatMinuteOfDay

/**
 * C · Inicio: una sola idea. En reposo, "LOST está atento"; en recorrido, el estado en un chip,
 * la hora de llegada y a dónde vas. Tocar el estado lleva al detalle (revelación progresiva).
 */
@Composable
fun HomeScreen(
    onOpenMonitoring: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    errorViewModel: ErrorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val errors by errorViewModel.uiState.collectAsStateWithLifecycle()
    val blockingError = errors.messages.firstOrNull()
    if (!state.isMonitoring && blockingError != null && !state.isDemoAvailable) {
        ErrorScreen(blockingError, iconFor(blockingError))
    } else {
        HomeContent(state, onOpenMonitoring, viewModel::onEvent)
    }
}

@Composable
private fun HomeContent(state: HomeUiState, onOpenMonitoring: () -> Unit, onEvent: (HomeUiEvent) -> Unit) {
    WatchScreenTemplate(ring = { BreathingStatusRing(state.status.tone, state.ringProgress) }) {
        if (state.isMonitoring) {
            MonitoringStatus(state, onOpenMonitoring)
            LostSecondaryButton(stringResource(R.string.home_stop_trip), {
                onEvent(HomeUiEvent.StopTrip)
            }, style = SecondaryButtonStyle.TEXT_ONLY)
        } else {
            IdleStatus()
            LostPrimaryButton(label = stringResource(R.string.home_start_trip), onClick = {
                onEvent(HomeUiEvent.StartTrip)
            })
        }
    }
}

@Composable
private fun IdleStatus() {
    LostIcon(StatusPresentation.ON_ROUTE.icon, null, tint = SemanticTokens.Status.onRoute)
    LostText(stringResource(R.string.home_idle_title), role = LostTextRole.TITLE)
    LostText(
        stringResource(R.string.home_idle_body),
        role = LostTextRole.CAPTION,
        color = SemanticTokens.Text.secondary
    )
}

@Composable
private fun MonitoringStatus(state: HomeUiState, onOpenMonitoring: () -> Unit) {
    LostChip(
        stringResource(state.status.label),
        icon = state.status.icon,
        tone = state.status.tone,
        onClick = onOpenMonitoring
    )
    val arrival = state.arrivalMinuteOfDay.takeUnless { state.isVoluntaryDetour || state.isNewPlace }
    arrival?.let { LostText(formatMinuteOfDay(it), role = LostTextRole.DISPLAY) }
    LostText(supportLine(state, hasArrival = arrival != null), role = LostTextRole.CAPTION)
}

@Composable
private fun supportLine(state: HomeUiState, hasArrival: Boolean): String = when {
    state.isVoluntaryDetour -> stringResource(R.string.home_walking_safely)
    state.isNewPlace -> state.routineName ?: stringResource(R.string.home_new_place)
    hasArrival && state.routineName != null -> stringResource(R.string.home_arriving_at, state.routineName)
    else -> stringResource(R.string.home_only_observing)
}

@LostDevicePreviews
@Composable
private fun HomeIdlePreview() {
    LostTheme { HomeContent(HomeUiState.idle(isDemoAvailable = true), {}, {}) }
}

@LostDevicePreviews
@Composable
private fun HomeMonitoringPreview() {
    val state = HomeUiState.idle(false).copy(
        isMonitoring = true,
        ringProgress = 0.3f,
        routineName = "U",
        arrivalMinuteOfDay = 435
    )
    LostTheme { HomeContent(state, {}, {}) }
}

@LostDevicePreviews
@Composable
private fun HomeDeviationPreview() {
    val state = HomeUiState.idle(
        false
    ).copy(isMonitoring = true, status = StatusPresentation.DEVIATION, ringProgress = 0.3f)
    LostTheme { HomeContent(state, {}, {}) }
}
