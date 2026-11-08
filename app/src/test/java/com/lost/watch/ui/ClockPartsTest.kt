package com.lost.watch.ui

import java.util.Locale
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ClockPartsTest {
    @Test
    fun `en espanol la parte del dia va aparte de la hora`() {
        val parts = splitDayPeriod(formatMinuteOfDay(5 * 60 + 51, Locale.forLanguageTag("es-CO")))
        assertEquals("5:51", parts.time)
        assertEquals(true, parts.dayPeriod?.contains("m."))
    }

    @Test
    fun `en ingles tambien se separa am y pm`() {
        val parts = splitDayPeriod(formatMinuteOfDay(19 * 60 + 5, Locale.US))
        assertEquals("7:05", parts.time)
        assertEquals("PM", parts.dayPeriod)
    }

    @Test
    fun `sin parte del dia la hora queda entera`() {
        assertEquals(ClockParts("17:40", null), splitDayPeriod("17:40"))
        assertEquals(ClockParts("오전 5:51", null), splitDayPeriod("오전 5:51"))
    }
}
