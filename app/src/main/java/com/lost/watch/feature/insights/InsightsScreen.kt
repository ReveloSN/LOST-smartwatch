package com.lost.watch.feature.insights

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.organisms.SegmentState
import com.lost.designsystem.components.organisms.SegmentedProgressRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import java.time.DayOfWeek

/**
 * 03.1 · Tu semana: el anillo (un segmento por día hábil), cuántos días y un resumen de una línea.
 * Las métricas de éxito pasan a un segundo nivel ([MetricsScreen]).
 */
@Composable
fun InsightsScreen(onOpenMetrics: () -> Unit, viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state?.let { InsightsContent(it, onOpenMetrics) }
}

@Composable
private fun InsightsContent(state: InsightsUiState, onOpenMetrics: () -> Unit) {
    val segments = state.weekDays.map { if (it in state.daysWithTrips) SegmentState.DONE else SegmentState.EMPTY }
    WatchScreenTemplate(ring = { SegmentedProgressRing(segments) }) {
        LostText(
            stringResource(R.string.routines_week),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostText(
            stringResource(R.string.insights_days_done, state.daysWithTrips.size, state.weekDays.size),
            role = LostTextRole.NUMBER
        )
        LostText(
            stringResource(
                R.string.insights_week_summary,
                pluralStringResource(R.plurals.insights_trips, state.tripsCompleted, state.tripsCompleted),
                pluralStringResource(R.plurals.insights_routines, state.routinesLearned, state.routinesLearned)
            ),
            role = LostTextRole.CAPTION
        )
        LostSecondaryButton(
            stringResource(R.string.insights_see_metrics),
            onOpenMetrics,
            style = SecondaryButtonStyle.TEXT_ONLY
        )
    }
}

@LostDevicePreviews
@Composable
private fun InsightsPreview() {
    val state = InsightsUiState(
        weekDays = listOf(
            DayOfWeek.MONDAY,
            DayOfWeek.TUESDAY,
            DayOfWeek.WEDNESDAY,
            DayOfWeek.THURSDAY,
            DayOfWeek.FRIDAY
        ),
        daysWithTrips = setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY),
        tripsCompleted = 6,
        routinesLearned = 1,
        metrics = emptyList()
    )
    LostTheme { InsightsContent(state) {} }
}
