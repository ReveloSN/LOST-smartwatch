package com.lost.watch.feature.places

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ReasonContent
import com.lost.designsystem.components.molecules.ReasonRow
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.detection.DeviationCause
import com.lost.domain.metrics.DisorientationPlace
import com.lost.domain.metrics.PlaceLabel
import com.lost.domain.model.GeoPoint
import com.lost.watch.R

/**
 * 03.2 · Lugares donde más te desorientas: dónde preguntó LOST en el último mes, cuántas veces y por qué.
 * Los nombres salen de lo que LOST ya sabe (destinos y rutinas), así la ubicación no sale del reloj.
 */
@Composable
fun PlacesScreen(viewModel: PlacesViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    PlacesContent(state)
}

@Composable
private fun PlacesContent(state: PlacesUiState) {
    val privacyNote = stringResource(R.string.places_private)
    val empty = stringResource(R.string.places_empty)
    ListTemplate(title = stringResource(R.string.places_title)) {
        if (state.isEmpty) {
            item(key = "empty") { LostText(empty, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary) }
        }
        state.places.forEachIndexed { index, place ->
            item(key = index) {
                ReasonRow(ReasonContent(causeIcon(place.mainCause), placeName(place.label), placeDetail(place)))
            }
        }
        item(key = "private") {
            LostText(privacyNote, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        }
    }
}

@LostDevicePreviews
@Composable
private fun PlacesPreview() {
    val places = listOf(
        DisorientationPlace(GeoPoint(0.0, 0.0), 2, DeviationCause.REPEATED_AREA, PlaceLabel.OnRoutine("Casa → U")),
        DisorientationPlace(GeoPoint(0.0, 0.0), 1, null, PlaceLabel.NearDestination("Bloque C"))
    )
    LostTheme { PlacesContent(PlacesUiState(isLoading = false, places = places)) }
}

@LostDevicePreviews
@Composable
private fun PlacesEmptyPreview() {
    LostTheme { PlacesContent(PlacesUiState(isLoading = false, places = emptyList())) }
}
