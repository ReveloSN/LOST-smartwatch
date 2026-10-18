package com.lost.watch.feature.demo

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AltRoute
import androidx.compose.material.icons.rounded.Construction
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

/** Elige un recorrido GPX para reproducir la historia completa en el reloj o el emulador. */
@Composable
fun DemoScreen(onStarted: () -> Unit, viewModel: DemoViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    DemoContent(state) { event ->
        viewModel.onEvent(event)
        onStarted()
    }
}

@Composable
private fun DemoContent(state: DemoUiState, onEvent: (DemoUiEvent) -> Unit) {
    ListTemplate(title = stringResource(R.string.demo_title)) {
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
    LostTheme { DemoContent(DemoUiState(DemoScenario.entries, null)) {} }
}
