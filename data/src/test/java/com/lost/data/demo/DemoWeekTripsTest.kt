package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DemoWeekTripsTest {
    private val zone = ZoneId.of("America/Bogota")
    private val trips = DemoWeekTrips(zone)
    private val route = listOf(
        GeoPoint(1.1664, -77.2993, timestampMillis = 0L),
        GeoPoint(1.2000, -77.2950, timestampMillis = 20 * MINUTE),
        GeoPoint(1.2325, -77.2934, timestampMillis = 35 * MINUTE)
    )

    @Test
    fun `los cinco viajes caen de lunes a viernes de la semana pasada`() {
        val sunday = LocalDate.of(2026, 11, 8)
        val days = (0 until DemoWeekTrips.DAYS).map { startOf(it, sunday).dayOfWeek }
        assertEquals(
            listOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY),
            days
        )
    }

    @Test
    fun `un miercoles toma los cinco dias laborables anteriores sin fines de semana`() {
        val wednesday = LocalDate.of(2026, 11, 11)
        val dates = (0 until DemoWeekTrips.DAYS).map { startOf(it, wednesday).toLocalDate() }
        assertEquals(LocalDate.of(2026, 11, 4), dates.first())
        assertEquals(LocalDate.of(2026, 11, 10), dates.last())
        assertTrue(dates.none { it.dayOfWeek == DayOfWeek.SATURDAY || it.dayOfWeek == DayOfWeek.SUNDAY })
    }

    @Test
    fun `sale cerca de las 6 40 y tarda parecido a la ruta de siempre`() {
        val monday = LocalDate.of(2026, 11, 9)
        (0 until DemoWeekTrips.DAYS).forEach { day ->
            val trip = trips.trip(route, day, monday)
            val departure = startOf(day, monday).toLocalTime()
            assertTrue(departure in LocalTime.of(6, 35)..LocalTime.of(6, 45), "salida $departure")
            val minutes = trip.durationMillis / MINUTE.toDouble()
            assertTrue(minutes in 33.0..37.0, "duración $minutes")
            assertEquals(route.map { it.latitude }, trip.points.map { it.latitude })
        }
    }

    private fun startOf(day: Int, today: LocalDate) =
        Instant.ofEpochMilli(trips.trip(route, day, today).startedAtMillis!!).atZone(zone)

    private companion object {
        const val MINUTE = 60_000L
    }
}
