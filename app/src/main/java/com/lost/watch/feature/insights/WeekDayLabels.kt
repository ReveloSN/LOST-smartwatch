package com.lost.watch.feature.insights

import androidx.annotation.StringRes
import com.lost.watch.R
import java.time.DayOfWeek

/**
 * Letra y nombre de cada día hábil. Salen de strings.xml (no de `DayOfWeek.getDisplayName`) para que
 * la letra sea la del mockup en cada idioma: "M" para miércoles, no la "X" de algunos calendarios.
 */
object WeekDayLabels {
    val workWeek: List<DayOfWeek> = listOf(
        DayOfWeek.MONDAY,
        DayOfWeek.TUESDAY,
        DayOfWeek.WEDNESDAY,
        DayOfWeek.THURSDAY,
        DayOfWeek.FRIDAY
    )

    private val initials = mapOf(
        DayOfWeek.MONDAY to R.string.weekday_initial_monday,
        DayOfWeek.TUESDAY to R.string.weekday_initial_tuesday,
        DayOfWeek.WEDNESDAY to R.string.weekday_initial_wednesday,
        DayOfWeek.THURSDAY to R.string.weekday_initial_thursday,
        DayOfWeek.FRIDAY to R.string.weekday_initial_friday
    )

    private val names = mapOf(
        DayOfWeek.MONDAY to R.string.weekday_monday,
        DayOfWeek.TUESDAY to R.string.weekday_tuesday,
        DayOfWeek.WEDNESDAY to R.string.weekday_wednesday,
        DayOfWeek.THURSDAY to R.string.weekday_thursday,
        DayOfWeek.FRIDAY to R.string.weekday_friday
    )

    @StringRes
    fun initialOf(day: DayOfWeek): Int = initials.getValue(day)

    @StringRes
    fun nameOf(day: DayOfWeek): Int = names.getValue(day)
}
