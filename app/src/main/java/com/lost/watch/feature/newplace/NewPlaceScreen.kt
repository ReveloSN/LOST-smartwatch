package com.lost.watch.feature.newplace

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostChip
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.PlaceKind
import com.lost.watch.R
import com.lost.watch.ui.PlaceIcons

/** H.1 · Modo lugar nuevo: "No conozco este lugar. Te aviso si das vueltas o vuelves al mismo punto." */
@Composable
fun NewPlaceScreen(onStarted: () -> Unit, viewModel: NewPlaceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val current = state ?: return
    LaunchedEffect(current.isStarted) { if (current.isStarted) onStarted() }
    NewPlaceContent(current) { viewModel.onEvent(NewPlaceUiEvent.Start) }
}

@Composable
private fun NewPlaceContent(state: NewPlaceUiState, onStart: () -> Unit) {
    WatchScreenTemplate {
        LostChip(stringResource(R.string.newplace_chip), icon = Icons.Rounded.Explore)
        LostIcon(PlaceIcons.of(state.kind), null, tint = SemanticTokens.Status.onRoute)
        LostText(state.destinationName, role = LostTextRole.TITLE)
        LostText(
            stringResource(R.string.newplace_body),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostPrimaryButton(stringResource(R.string.newplace_start), onStart)
    }
}

@LostDevicePreviews
@Composable
private fun NewPlacePreview() {
    LostTheme { NewPlaceContent(NewPlaceUiState("Consultorio 304", PlaceKind.HOSPITAL, false)) {} }
}
