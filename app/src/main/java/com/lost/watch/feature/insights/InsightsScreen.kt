package com.lost.watch.feature.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.organisms.SegmentState
import com.lost.designsystem.components.organisms.SegmentedProgressRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale

/** 03.1 · Tu semana en el anillo (un segmento por día hábil) + 13 · métricas de éxito. */
@Composable
fun InsightsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state?.let { InsightsContent(it) }
}

@Composable
private fun InsightsContent(state: InsightsUiState) {
    val segments = state.weekDays.map { if (it in state.daysWithTrips) SegmentState.DONE else SegmentState.EMPTY }
    val locale = LocalConfiguration.current.locales[0]
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
        WeekLetters(state, locale)
        LostText(
            stringResource(R.string.insights_week_summary, state.tripsCompleted, state.routinesLearned),
            role = LostTextRole.CAPTION
        )
        state.metrics.forEach { MetricRow(it) }
    }
}

@Composable
private fun WeekLetters(state: InsightsUiState, locale: Locale) {
    Row(horizontalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)) {
        state.weekDays.forEach { day ->
            val color = if (day in state.daysWithTrips) SemanticTokens.Status.onRoute else SemanticTokens.Text.secondary
            LostText(day.getDisplayName(TextStyle.NARROW, locale), role = LostTextRole.CAPTION, color = color)
        }
    }
}

@Composable
private fun MetricRow(line: MetricLine) {
    val value = when (val metric = line.value) {
        is MetricValue.Percent -> stringResource(R.string.insights_percent, metric.value)

        is MetricValue.Seconds -> stringResource(
            R.string.insights_seconds,
            String.format(LocalConfiguration.current.locales[0], "%.1f", metric.value)
        )

        MetricValue.NoData -> stringResource(R.string.insights_no_data)
    }
    val goal = if (line.meetsGoal) {
        stringResource(
            R.string.insights_goal_met
        )
    } else {
        stringResource(R.string.insights_goal, stringResource(line.goal))
    }
    LostText(stringResource(line.label), role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
    LostText(
        "$value · $goal",
        role = LostTextRole.TITLE,
        color = if (line.meetsGoal) SemanticTokens.Status.onRoute else SemanticTokens.Text.primary
    )
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
        metrics = listOf(
            MetricLine(R.string.insights_success, MetricValue.Percent(92), true, R.string.insights_goal_success)
        )
    )
    LostTheme { InsightsContent(state) }
}
