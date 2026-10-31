package com.lost.watch.feature.destinations

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.Place
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.ScalingLazyListScope
import androidx.wear.compose.foundation.lazy.items
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.PlaceKind
import com.lost.watch.R
import com.lost.watch.feature.errors.uiMessageText
import com.lost.watch.ui.PlaceIcons

/** 02.1 / 02.2 · Destinos: micrófono siempre arriba, sugeridos por la IA y recientes. */
@Composable
fun DestinationsScreen(onOpenDestination: (String) -> Unit, viewModel: DestinationsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.search.matchedId) {
        state.search.matchedId?.let {
            viewModel.onEvent(DestinationsUiEvent.MatchConsumed)
            onOpenDestination(it)
        }
    }
    val voiceLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (result.resultCode == Activity.RESULT_OK &&
            spoken != null
        ) {
            viewModel.onEvent(DestinationsUiEvent.VoiceQuery(spoken))
        }
    }
    val prompt = stringResource(R.string.destinations_voice)
    DestinationsContent(state, onOpenDestination, viewModel::onEvent) {
        voiceLauncher.launch(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        )
    }
}

@Composable
private fun DestinationsContent(
    state: DestinationsUiState,
    onOpen: (String) -> Unit,
    onEvent: (DestinationsUiEvent) -> Unit,
    onVoice: () -> Unit
) {
    ListTemplate(title = stringResource(R.string.nav_destinations)) {
        item(key = "voice") {
            LostListItem(
                ListItemContent(
                    Icons.Rounded.Mic,
                    stringResource(R.string.destinations_voice),
                    stringResource(R.string.destinations_voice_example)
                ),
                onClick = onVoice
            )
        }
        searchRows(state.search, onEvent)
        if (state.items.isEmpty()) {
            item(key = "empty") { LostText(stringResource(R.string.destinations_empty), role = LostTextRole.CAPTION) }
        }
        items(state.items, key = { it.id }) { item -> DestinationRow(item) { onOpen(item.id) } }
    }
}

/** Lo que pasó con el último dictado: buscando, lugares reales encontrados, nada o sin conexión. */
private fun ScalingLazyListScope.searchRows(search: DestinationSearchState, onEvent: (DestinationsUiEvent) -> Unit) {
    if (search.isSearching) item(key = "searching") { Caption(stringResource(R.string.destinations_searching)) }
    search.notFoundQuery?.let { query ->
        item(key = "not-found") { Caption(stringResource(R.string.destinations_not_found, query), isWarning = true) }
    }
    search.error?.let { message ->
        item(key = "search-error") { Caption(uiMessageText(message).joined(), isWarning = true) }
    }
    if (search.results.isNotEmpty()) {
        item(key = "results") { Caption(stringResource(R.string.destinations_results)) }
        items(search.results, key = { "${it.location.latitude},${it.location.longitude}" }) { place ->
            LostListItem(
                ListItemContent(Icons.Rounded.Place, place.name, place.detail),
                onClick = { onEvent(DestinationsUiEvent.ChoosePlace(place)) }
            )
        }
        item(key = "privacy") { Caption(stringResource(R.string.destinations_search_privacy)) }
    }
}

@Composable
private fun Caption(text: String, isWarning: Boolean = false) {
    LostText(
        text,
        role = LostTextRole.CAPTION,
        color = if (isWarning) SemanticTokens.Status.deviation else SemanticTokens.Text.secondary
    )
}

@Composable
private fun DestinationRow(item: DestinationItem, onClick: () -> Unit) {
    val tag = when {
        item.isSuggested -> stringResource(R.string.destinations_suggested)
        item.isRecent -> stringResource(R.string.destinations_recent)
        else -> null
    }
    LostListItem(ListItemContent(PlaceIcons.of(item.kind), item.name, tag), onClick = onClick)
}

@LostDevicePreviews
@Composable
private fun DestinationsPreview() {
    val items = listOf(
        DestinationItem("1", "Laboratorio 3, Bloque C", PlaceKind.UNIVERSITY, isSuggested = true, isRecent = false),
        DestinationItem("2", "Biblioteca Central", PlaceKind.LIBRARY, isSuggested = false, isRecent = true)
    )
    LostTheme { DestinationsContent(DestinationsUiState(items, DestinationSearchState()), {}, {}, {}) }
}
