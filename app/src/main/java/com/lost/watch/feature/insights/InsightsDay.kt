package com.lost.watch.feature.insights

import java.time.DayOfWeek

/** Un día hábil en el anillo de Tu semana. */
data class InsightsDay(val day: DayOfWeek, val status: WeekDayStatus)
