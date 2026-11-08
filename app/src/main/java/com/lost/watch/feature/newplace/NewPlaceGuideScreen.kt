package com.lost.watch.feature.newplace

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.molecules.DistanceContent
import com.lost.designsystem.components.molecules.DistanceIndicator
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.watch.R
import com.lost.watch.ui.arrowDescription
import com.lost.watch.ui.formatDistance

/** H.2 · Flecha grande hacia el destino, distancia y nombre. Al llegar muestra 02.4. */
@Composable
fun NewPlaceGuideScreen(onFinished: () -> Unit, viewModel: NewPlaceGuideViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    if (state.hasArrived) {
        ArrivalContent(state.destinationName, onSave = { viewModel.onEvent(NewPlaceUiEvent.SaveDestination) }) {
            viewModel.onEvent(NewPlaceUiEvent.Finish)
            onFinished()
        }
    } else {
        GuideContent(state) {
            viewModel.onEvent(NewPlaceUiEvent.Finish)
            onFinished()
        }
    }
}

@Composable
private fun GuideContent(state: NewPlaceGuideUiState, onStop: () -> Unit) {
    WatchScreenTemplate {
        DistanceIndicator(
            DistanceContent(
                distance = state.distanceMeters?.let { formatDistance(it) }.orEmpty(),
                detail = state.destinationName.orEmpty(),
                arrowRotationDegrees = state.arrowDegrees,
                arrowDescription = arrowDescription(state.bearingDegrees)
            )
        )
        LostSecondaryButton(stringResource(R.string.home_stop_trip), onStop, style = SecondaryButtonStyle.TEXT_ONLY)
    }
}

@LostDevicePreviews
@Composable
private fun GuidePreview() {
    LostTheme { GuideContent(NewPlaceGuideUiState("Consultorio 304", 40.0, -60f, false)) {} }
}
