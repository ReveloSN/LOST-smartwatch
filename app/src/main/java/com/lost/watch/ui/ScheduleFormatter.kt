package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.lost.domain.model.RoutineSchedule
import com.lost.watch.R
import java.time.DayOfWeek
import java.time.format.TextStyle

/** "L–V · 6:40 · 35 min": días, hora de salida y duración habitual. */
@Composable
fun scheduleText(schedule: RoutineSchedule, durationMinutes: Int): String = stringResource(
    R.string.routine_schedule,
    daysText(schedule.days),
    formatMinuteOfDay(schedule.departureMinuteOfDay),
    durationMinutes
)

@Composable
private fun daysText(days: Set<DayOfWeek>): String {
    if (days == RoutineSchedule.WEEKDAYS) return stringResource(R.string.routine_weekdays)
    val locale = LocalConfiguration.current.locales[0]
    return days.sorted().joinToString(", ") { it.getDisplayName(TextStyle.SHORT, locale) }
}
