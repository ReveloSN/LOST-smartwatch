package com.lost.domain.model

import java.time.DayOfWeek
import java.util.EnumSet

/** Cuándo ocurre una rutina: días de la semana y minuto del día en que suele empezar. */
data class RoutineSchedule(val days: Set<DayOfWeek>, val departureMinuteOfDay: Int) {
    fun occursOn(day: DayOfWeek): Boolean = day in days

    companion object {
        val WEEKDAYS: Set<DayOfWeek> = EnumSet.range(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
    }
}
