package com.lost.data.persistence

import com.lost.data.persistence.codec.DayOfWeekSetCodec
import com.lost.data.persistence.codec.GeoPointListCodec
import com.lost.domain.model.GeoPoint
import java.time.DayOfWeek
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class CodecsTest {
    @Test
    fun `los puntos se guardan y se leen sin perder datos`() {
        val points = listOf(GeoPoint(4.6097, -74.0817, 1_000L), GeoPoint(4.61, -74.08, 2_000L))
        assertEquals(points, GeoPointListCodec.decode(GeoPointListCodec.encode(points)))
    }

    @Test
    fun `un punto danado se descarta sin romper el resto`() {
        val decoded = GeoPointListCodec.decode("4.6,-74.0,1;basura;4.7,-74.1,2")
        assertEquals(2, decoded.size)
    }

    @Test
    fun `una lista vacia se guarda como texto vacio`() {
        assertTrue(GeoPointListCodec.decode(GeoPointListCodec.encode(emptyList())).isEmpty())
    }

    @Test
    fun `los dias se guardan y se leen`() {
        val days = setOf(DayOfWeek.MONDAY, DayOfWeek.FRIDAY)
        assertEquals(days, DayOfWeekSetCodec.decode(DayOfWeekSetCodec.encode(days)))
        assertTrue(DayOfWeekSetCodec.decode("").isEmpty())
    }
}
