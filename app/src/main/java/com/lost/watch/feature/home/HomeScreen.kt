package com.lost.watch.feature.home

import androidx.compose.foundation.clickable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.PlaceKind
import com.lost.watch.R
import com.lost.watch.feature.errors.ErrorScreen
import com.lost.watch.feature.errors.ErrorViewModel
import com.lost.watch.feature.errors.iconFor
import com.lost.watch.ui.PlaceIcons
import com.lost.watch.ui.StatusPresentation
import com.lost.watch.ui.formatMinuteOfDay

/** C · Inicio: el anillo respira sobre la ruta de siempre; tocar el estado explica qué nota LOST. */
@Composable
fun HomeScreen(
    onOpenMonitoring: () -> Unit,
    onOpenDemo: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel(),
    errorViewModel: ErrorViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val errors by errorViewModel.uiState.collectAsStateWithLifecycle()
    val blockingError = errors.messages.firstOrNull()
    if (!state.isMonitoring && blockingError != null && !state.isDemoAvailable) {
        ErrorScreen(blockingError, iconFor(blockingError))
    } else {
        HomeContent(state, onOpenMonitoring, onOpenDemo, viewModel::onEvent)
    }
}

@Composable
private fun HomeContent(
    state: HomeUiState,
    onOpenMonitoring: () -> Unit,
    onOpenDemo: () -> Unit,
    onEvent: (HomeUiEvent) -> Unit
) {
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
            if (state.isDemoAvailable) {
                LostSecondaryButton(
                    stringResource(R.string.settings_demo),
                    onOpenDemo,
                    style = SecondaryButtonStyle.TEXT_ONLY
                )
            }
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
    val statusLabel = stringResource(state.status.label)
    LostIcon(state.status.icon, null, tint = SemanticTokens.Status.of(state.status.tone))
    LostText(
        text = statusLabel,
        role = LostTextRole.TITLE,
        color = SemanticTokens.Status.of(state.status.tone),
        modifier = Modifier.clickable(role = Role.Button, onClick = onOpenMonitoring)
    )
    when {
        state.isVoluntaryDetour -> LostText(stringResource(R.string.home_walking_safely), role = LostTextRole.CAPTION)
        state.isNewPlace -> NewPlaceLine(state)
        else -> RoutineLine(state)
    }
}

@Composable
private fun RoutineLine(state: HomeUiState) {
    state.arrivalMinuteOfDay?.let { LostText(formatMinuteOfDay(it), role = LostTextRole.DISPLAY) }
    state.routineName?.let { name ->
        LostIcon(PlaceIcons.of(state.routineKind ?: PlaceKind.OTHER), null, size = ComponentTokens.Icon.small)
        LostText(stringResource(R.string.home_heading_to, name), role = LostTextRole.CAPTION)
    }
    if (state.expectedMinutes > 0) {
        LostText(
            stringResource(R.string.home_progress, state.elapsedMinutes, state.expectedMinutes),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}

@Composable
private fun NewPlaceLine(state: HomeUiState) {
    LostIcon(Icons.Rounded.Explore, null, size = ComponentTokens.Icon.small)
    LostText(state.routineName ?: stringResource(R.string.home_new_place), role = LostTextRole.CAPTION)
}

@LostDevicePreviews
@Composable
private fun HomeIdlePreview() {
    LostTheme { HomeContent(HomeUiState.idle(isDemoAvailable = true), {}, {}, {}) }
}

@LostDevicePreviews
@Composable
private fun HomeMonitoringPreview() {
    val state = HomeUiState.idle(false).copy(
        isMonitoring = true,
        ringProgress = 0.3f,
        routineName = "U",
        routineKind = PlaceKind.UNIVERSITY,
        arrivalMinuteOfDay = 435,
        elapsedMinutes = 10,
        expectedMinutes = 35
    )
    LostTheme { HomeContent(state, {}, {}, {}) }
}

@LostDevicePreviews
@Composable
private fun HomeDeviationPreview() {
    val state = HomeUiState.idle(
        false
    ).copy(isMonitoring = true, status = StatusPresentation.DEVIATION, ringProgress = 0.3f)
    LostTheme { HomeContent(state, {}, {}, {}) }
}
