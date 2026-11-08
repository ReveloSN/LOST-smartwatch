package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId

/**
 * Los cinco viajes de la semana pasada con los que la demo enseña la rutina: la ruta de siempre, un día
 * laborable distinto cada vez, saliendo cerca de las 6:40 y tardando un poco más o un poco menos, como
 * pasa en la vida real. Así el aprendizaje de LOST deduce solo los días, la hora y la duración.
 */
class DemoWeekTrips(private val zoneId: ZoneId = ZoneId.systemDefault()) {
    /** El viaje del día [dayIndex] (0 = el más antiguo de los últimos cinco días laborables antes de [today]). */
    fun trip(route: List<GeoPoint>, dayIndex: Int, today: LocalDate): Trip {
        val variation = VARIATIONS[dayIndex % VARIATIONS.size]
        val day = lastWeekdaysBefore(today)[dayIndex % DAYS]
        val departure = day.atTime(DEPARTURE.plusMinutes(variation.departureShiftMinutes)).atZone(zoneId)
        val departureMillis = departure.toInstant().toEpochMilli()
        val firstMillis = route.first().timestampMillis
        val points = route.map { point ->
            val elapsed = (point.timestampMillis - firstMillis) * variation.durationFactor
            point.copy(timestampMillis = departureMillis + elapsed.toLong())
        }
        return Trip("demo-day-$dayIndex", points)
    }

    private fun lastWeekdaysBefore(today: LocalDate): List<LocalDate> =
        generateSequence(today.minusDays(1)) { it.minusDays(1) }
            .filter { it.dayOfWeek != DayOfWeek.SATURDAY && it.dayOfWeek != DayOfWeek.SUNDAY }
            .take(DAYS)
            .toList()
            .reversed()

    private data class Variation(val departureShiftMinutes: Long, val durationFactor: Double)

    companion object {
        /** Los días que necesita LOST para confirmar una rutina (`RoutineConfirmationPolicy`). */
        const val DAYS = 5

        private val DEPARTURE: LocalTime = LocalTime.of(6, 40)

        @Suppress("MagicNumber") // datos de ejemplo de la demo, no umbrales del motor
        private val VARIATIONS = listOf(
            Variation(departureShiftMinutes = 0, durationFactor = 1.0),
            Variation(departureShiftMinutes = 2, durationFactor = 1.04),
            Variation(departureShiftMinutes = -1, durationFactor = 0.97),
            Variation(departureShiftMinutes = 3, durationFactor = 1.02),
            Variation(departureShiftMinutes = -2, durationFactor = 0.99)
        )
    }
}
