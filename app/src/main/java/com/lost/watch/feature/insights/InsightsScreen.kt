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
import com.lost.designsystem.components.organisms.SegmentLabel
import com.lost.designsystem.components.organisms.SegmentState
import com.lost.designsystem.components.organisms.SegmentedProgressRing
import com.lost.designsystem.components.organisms.SegmentedRingLegend
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import java.time.DayOfWeek

/**
 * 03.1 · Tu semana: el anillo (un segmento por día hábil, en ámbar el día con desvío), cuántos días,
 * un resumen de una línea y la letra de cada día. Las métricas de éxito pasan a un segundo nivel
 * ([MetricsScreen]). El desvío nunca se dice solo con color: el resumen lo cuenta en palabras. También lleva
 * a dónde preguntó LOST ([com.lost.watch.feature.places.PlacesScreen]).
 */
@Composable
fun InsightsScreen(
    onOpenMetrics: () -> Unit,
    onOpenPlaces: () -> Unit,
    viewModel: InsightsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state?.let { InsightsContent(it, onOpenMetrics, onOpenPlaces) }
}

@Composable
private fun InsightsContent(state: InsightsUiState, onOpenMetrics: () -> Unit, onOpenPlaces: () -> Unit) {
    WatchScreenTemplate(ring = { SegmentedProgressRing(state.days.map { segmentStateOf(it.status) }) }) {
        LostText(
            stringResource(R.string.routines_week),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostText(
            stringResource(R.string.insights_days_done, state.daysWithTrips, state.days.size),
            role = LostTextRole.NUMBER
        )
        LostText(
            stringResource(
                R.string.insights_week_summary,
                pluralStringResource(R.plurals.insights_trips, state.tripsCompleted, state.tripsCompleted),
                deviationSummary(state.daysWithDeviation)
            ),
            role = LostTextRole.CAPTION
        )
        WeekLegend(state.days)
        WeekActions(onOpenPlaces, onOpenMetrics)
    }
}

/** A dónde ir desde Tu semana: los lugares donde LOST preguntó (o su estado vacío) y las métricas. */
@Composable
private fun WeekActions(onOpenPlaces: () -> Unit, onOpenMetrics: () -> Unit) {
    LostSecondaryButton(
        stringResource(R.string.insights_see_places),
        onOpenPlaces,
        style = SecondaryButtonStyle.TEXT_ONLY
    )
    LostSecondaryButton(
        stringResource(R.string.insights_see_metrics),
        onOpenMetrics,
        style = SecondaryButtonStyle.TEXT_ONLY
    )
}

@Composable
private fun deviationSummary(daysWithDeviation: Int): String = if (daysWithDeviation == 0) {
    stringResource(R.string.insights_no_deviations)
} else {
    pluralStringResource(R.plurals.insights_deviation_days, daysWithDeviation, daysWithDeviation)
}

@Composable
private fun WeekLegend(days: List<InsightsDay>) {
    val labels = days.map { SegmentLabel(stringResource(WeekDayLabels.initialOf(it.day)), segmentStateOf(it.status)) }
    val description = days.map { dayDescription(it) }.joinToString(separator = ". ")
    SegmentedRingLegend(labels, description)
}

@Composable
private fun dayDescription(day: InsightsDay): String {
    val name = stringResource(WeekDayLabels.nameOf(day.day))
    return when (day.status) {
        WeekDayStatus.NO_TRIP -> stringResource(R.string.insights_day_no_trip, name)
        WeekDayStatus.ON_ROUTE -> stringResource(R.string.insights_day_on_route, name)
        WeekDayStatus.DEVIATED -> stringResource(R.string.insights_day_deviated, name)
    }
}

private fun segmentStateOf(status: WeekDayStatus): SegmentState = when (status) {
    WeekDayStatus.NO_TRIP -> SegmentState.EMPTY
    WeekDayStatus.ON_ROUTE -> SegmentState.DONE
    WeekDayStatus.DEVIATED -> SegmentState.FLAGGED
}

@LostDevicePreviews
@Composable
private fun InsightsPreview() {
    val state = InsightsUiState(
        days = listOf(
            InsightsDay(DayOfWeek.MONDAY, WeekDayStatus.ON_ROUTE),
            InsightsDay(DayOfWeek.TUESDAY, WeekDayStatus.DEVIATED),
            InsightsDay(DayOfWeek.WEDNESDAY, WeekDayStatus.ON_ROUTE),
            InsightsDay(DayOfWeek.THURSDAY, WeekDayStatus.NO_TRIP),
            InsightsDay(DayOfWeek.FRIDAY, WeekDayStatus.NO_TRIP)
        ),
        tripsCompleted = 6,
        metrics = emptyList()
    )
    LostTheme { InsightsContent(state, {}, {}) }
}
