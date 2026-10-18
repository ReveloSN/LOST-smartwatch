package com.lost.watch.feature.routines

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoAwesome
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.DirectionsWalk
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.lazy.items
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.molecules.RoutineCard
import com.lost.designsystem.components.molecules.RoutineCardContent
import com.lost.designsystem.components.organisms.SegmentState
import com.lost.designsystem.components.organisms.SegmentedProgressRing
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.RoutineSchedule
import com.lost.watch.R
import com.lost.watch.ui.PlaceIcons
import com.lost.watch.ui.scheduleText

private const val LEARNING_REPETITIONS = 5

/** B.2 · Mis recorridos: lista circular; el elemento enfocado crece al centro. */
@Composable
fun RoutinesScreen(
    onOpenRoutine: (String) -> Unit,
    onOpenLearning: (String) -> Unit,
    onOpenWeek: () -> Unit,
    viewModel: RoutinesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    RoutinesContent(state, onOpen = { item ->
        if (item.isConfirmed) onOpenRoutine(item.id) else onOpenLearning(item.id)
    }, onOpenWeek)
}

@Composable
private fun RoutinesContent(state: RoutinesUiState, onOpen: (RoutineItem) -> Unit, onOpenWeek: () -> Unit) {
    if (state.isEmpty) return EmptyRoutines()
    ListTemplate(title = stringResource(R.string.nav_routines)) {
        items(state.items, key = { it.id }) { item -> RoutineRow(item) { onOpen(item) } }
        item(key = "week") {
            LostListItem(
                ListItemContent(Icons.Rounded.CalendarToday, stringResource(R.string.routines_week)),
                onClick = onOpenWeek
            )
        }
    }
}

@Composable
private fun RoutineRow(item: RoutineItem, onClick: () -> Unit) {
    val title = item.name ?: stringResource(R.string.routine_new)
    val schedule = scheduleText(item.schedule, item.durationMinutes)
    val detail = if (item.isConfirmed) schedule else "$schedule · ${stringResource(R.string.routine_confirm_question)}"
    val icon = if (item.isConfirmed) PlaceIcons.of(item.kind) else Icons.Rounded.AutoAwesome
    RoutineCard(RoutineCardContent(icon, title, detail), onClick = onClick)
}

/** VACÍO.1 · El anillo segmentado vacío anticipa el aprendizaje. */
@Composable
private fun EmptyRoutines() {
    WatchScreenTemplate(ring = { SegmentedProgressRing(List(LEARNING_REPETITIONS) { SegmentState.EMPTY }) }) {
        LostIcon(Icons.Rounded.DirectionsWalk, null, tint = SemanticTokens.Status.onRoute)
        LostText(stringResource(R.string.routines_empty_title), role = LostTextRole.TITLE)
        LostText(
            stringResource(R.string.routines_empty_body),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}

@LostDevicePreviews
@Composable
private fun RoutinesPreview() {
    val schedule = RoutineSchedule(RoutineSchedule.WEEKDAYS, 400)
    val items = listOf(
        RoutineItem("1", "Casa → U", PlaceKind.UNIVERSITY, schedule, 35, 5, true),
        RoutineItem("2", null, PlaceKind.OTHER, schedule, 40, 4, false)
    )
    LostTheme { RoutinesContent(RoutinesUiState(false, items), {}, {}) }
}

@LostDevicePreviews
@Composable
private fun RoutinesEmptyPreview() {
    LostTheme { RoutinesContent(RoutinesUiState(false, emptyList()), {}, {}) }
}
