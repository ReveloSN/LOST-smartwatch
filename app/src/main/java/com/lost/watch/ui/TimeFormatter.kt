package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

private const val MINUTES_PER_HOUR = 60
private const val MINUTES_PER_DAY = 24 * MINUTES_PER_HOUR

/** Hora corta según el idioma del usuario ("7:15" o "7:15 a. m."). */
@Composable
fun formatMinuteOfDay(minuteOfDay: Int): String = formatMinuteOfDay(minuteOfDay, LocalConfiguration.current.locales[0])

/** La misma hora corta fuera de Compose (complicación y vista rápida). */
fun formatMinuteOfDay(minuteOfDay: Int, locale: Locale): String {
    val minutes = ((minuteOfDay % MINUTES_PER_DAY) + MINUTES_PER_DAY) % MINUTES_PER_DAY
    val time = LocalTime.of(minutes / MINUTES_PER_HOUR, minutes % MINUTES_PER_HOUR)
    return time.format(DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(locale))
}
