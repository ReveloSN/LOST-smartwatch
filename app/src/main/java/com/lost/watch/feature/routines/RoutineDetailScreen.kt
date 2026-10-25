package com.lost.watch.feature.routines

import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.organisms.RouteComparisonContent
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import com.lost.watch.ui.RouteProjector
import com.lost.watch.ui.map.AdaptiveMapSizing
import com.lost.watch.ui.map.AdaptiveRouteMap
import com.lost.watch.ui.map.MapUiState
import com.lost.watch.ui.scheduleText

private const val MAP_WIDTH_FRACTION = 0.75f

/** B.3 · Detalle del recorrido: trayectoria de referencia y acciones (guardar, renombrar por voz, borrar). */
@Composable
fun RoutineDetailScreen(onDeleted: () -> Unit, viewModel: RoutineDetailViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(state.isDeleted) { if (state.isDeleted) onDeleted() }
    val renameLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        val spoken = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
        if (result.resultCode == Activity.RESULT_OK && spoken != null) viewModel.onEvent(RoutinesUiEvent.Rename(spoken))
    }
    val prompt = stringResource(R.string.routine_rename_prompt)
    val item = state.item ?: return
    RoutineDetailContent(item, state, viewModel::onEvent) {
        renameLauncher.launch(
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                .putExtra(RecognizerIntent.EXTRA_PROMPT, prompt)
        )
    }
}

@Composable
private fun RoutineDetailContent(
    item: RoutineItem,
    state: RoutineDetailUiState,
    onEvent: (RoutinesUiEvent) -> Unit,
    onRename: () -> Unit
) {
    val title = item.name ?: stringResource(R.string.routine_new)
    val projected = remember(state.path) { RouteProjector().project(state.path, emptyList()) }
    WatchScreenTemplate {
        val square = Modifier.fillMaxWidth(MAP_WIDTH_FRACTION).aspectRatio(1f)
            .clip(RoundedCornerShape(SemanticTokens.Shape.container))
        AdaptiveRouteMap(
            state = state.map,
            simple = RouteComparisonContent(
                projected.habitual,
                emptyList(),
                null,
                StatusTone.ON_ROUTE,
                stringResource(R.string.cd_routine_map, title)
            ),
            sizing = AdaptiveMapSizing(real = square, simple = square),
            showAttribution = false
        )
        MapNote(state.map)
        LostText(title, role = LostTextRole.TITLE)
        LostText(
            scheduleText(item.schedule, item.durationMinutes),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        RoutineActionButtons(item, state.isDeleteArmed, onEvent, onRename)
    }
}

/** Bajo el mapa: la atribución de OpenStreetMap o, sin conexión, qué muestra LOST en su lugar. */
@Composable
private fun MapNote(map: MapUiState) {
    val note = when (map) {
        is MapUiState.Real -> stringResource(R.string.map_attribution)
        is MapUiState.Unavailable -> stringResource(map.message.whatToDo)
        MapUiState.Simple -> null
    }
    note?.let { LostText(it, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary) }
}

@Composable
private fun RoutineActionButtons(
    item: RoutineItem,
    isDeleteArmed: Boolean,
    onEvent: (RoutinesUiEvent) -> Unit,
    onRename: () -> Unit
) {
    if (!item.isConfirmed) {
        LostPrimaryButton(stringResource(R.string.routine_save), {
            onEvent(RoutinesUiEvent.Confirm)
        })
    }
    if (item.isConfirmed) LostSecondaryButton(stringResource(R.string.routine_rename), onRename)
    val deleteLabel = if (isDeleteArmed) R.string.routine_delete_confirm else R.string.routine_delete
    LostSecondaryButton(stringResource(deleteLabel), {
        onEvent(RoutinesUiEvent.DeleteTapped)
    }, style = SecondaryButtonStyle.TEXT_ONLY)
}
