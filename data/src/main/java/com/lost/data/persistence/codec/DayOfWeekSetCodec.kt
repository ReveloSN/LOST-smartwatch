package com.lost.data.persistence.codec

import java.time.DayOfWeek
import java.util.EnumSet

/** Guarda los días de una rutina como "MONDAY,TUESDAY" y los lee como `EnumSet`. */
object DayOfWeekSetCodec {
    private const val SEPARATOR = ","

    fun encode(days: Set<DayOfWeek>): String = days.sorted().joinToString(SEPARATOR) { it.name }

    fun decode(text: String): Set<DayOfWeek> {
        val days = text.split(SEPARATOR).mapNotNull { name -> DayOfWeek.entries.firstOrNull { it.name == name } }
        return if (days.isEmpty()) EnumSet.noneOf(DayOfWeek::class.java) else EnumSet.copyOf(days)
    }
}
