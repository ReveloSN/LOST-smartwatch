package com.lost.watch.feature.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AltRoute
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Construction
import androidx.compose.material.icons.rounded.EventRepeat
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import com.lost.data.demo.DemoScenario
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.watch.R

/**
 * Primero, LOST aprende la ruta de siempre a la vista (un día por toque, como lo vería una persona nueva);
 * después, cada recorrido GPX reproduce una historia completa en el reloj o el emulador. Al elegir uno se
 * abre el monitoreo ([onStarted]): el mapa con la ruta de siempre y la de hoy, en vivo.
 */
@Composable
fun DemoScreen(onStarted: () -> Unit, onStopped: () -> Unit, viewModel: DemoViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DemoContent(state) { event ->
        viewModel.onEvent(event)
        when (event) {
            is DemoUiEvent.Play -> onStarted()
            DemoUiEvent.Stop -> onStopped()
            DemoUiEvent.LearnNextDay -> Unit
        }
    }
}

@Composable
private fun DemoContent(state: DemoUiState, onEvent: (DemoUiEvent) -> Unit) {
    ListTemplate(title = stringResource(R.string.demo_title)) {
        item(key = "learn") { LearnRoutineItem(state, onEvent) }
        items(state.scenarios, key = { it.name }) { scenario ->
            LostListItem(ListItemContent(iconFor(scenario), stringResource(labelFor(scenario))), onClick = {
                onEvent(DemoUiEvent.Play(scenario))
            })
        }
        if (state.playing != null) {
            item(key = "stop") {
                LostListItem(ListItemContent(Icons.Rounded.Stop, stringResource(R.string.demo_stop)), onClick = {
                    onEvent(DemoUiEvent.Stop)
                })
            }
        }
    }
}

@Composable
private fun LearnRoutineItem(state: DemoUiState, onEvent: (DemoUiEvent) -> Unit) {
    val progress = stringResource(R.string.demo_learn_progress, state.learnedDays, state.daysToLearn)
    if (state.isRoutineLearned) {
        LostListItem(ListItemContent(Icons.Rounded.CheckCircle, stringResource(R.string.demo_learned), progress), {})
    } else {
        LostListItem(ListItemContent(Icons.Rounded.EventRepeat, stringResource(R.string.demo_learn_day), progress), {
            onEvent(DemoUiEvent.LearnNextDay)
        })
    }
}

private fun labelFor(scenario: DemoScenario) = when (scenario) {
    DemoScenario.NORMAL_ROUTINE -> R.string.demo_normal
    DemoScenario.VOLUNTARY_DETOUR -> R.string.demo_voluntary
    DemoScenario.DISORIENTATION -> R.string.demo_disorientation
    DemoScenario.NEW_PLACE -> R.string.demo_new_place
}

private fun iconFor(scenario: DemoScenario) = when (scenario) {
    DemoScenario.NORMAL_ROUTINE -> Icons.Rounded.School
    DemoScenario.VOLUNTARY_DETOUR -> Icons.Rounded.AltRoute
    DemoScenario.DISORIENTATION -> Icons.Rounded.Construction
    DemoScenario.NEW_PLACE -> Icons.Rounded.LocalHospital
}

@LostDevicePreviews
@Composable
private fun DemoPreview() {
    LostTheme { DemoContent(DemoUiState(DemoScenario.entries, null, learnedDays = 3, daysToLearn = 5)) {} }
}
