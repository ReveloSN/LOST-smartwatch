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

/** Una hora corta partida en la hora en sí ("5:51") y, si el idioma la usa, la parte del día ("a. m."). */
data class ClockParts(val time: String, val dayPeriod: String?)

private val TIME_THEN_DAY_PERIOD = Regex("""^(\d{1,2}[:.]\d{2})[\s\u00A0\u202F]+(\S.*)$""")

/**
 * Separa "5:51 a. m." en "5:51" y "a. m." para dibujar la hora grande y la parte del día pequeña: en una
 * sola línea grande "a. m." no cabe en el reloj y la hora se cortaba ("5:51 a…"). Si el idioma pone la
 * parte del día antes o no la usa, la hora queda entera.
 */
fun splitDayPeriod(formatted: String): ClockParts = TIME_THEN_DAY_PERIOD.matchEntire(formatted)
    ?.let { ClockParts(it.groupValues[1], it.groupValues[2]) }
    ?: ClockParts(formatted, null)
