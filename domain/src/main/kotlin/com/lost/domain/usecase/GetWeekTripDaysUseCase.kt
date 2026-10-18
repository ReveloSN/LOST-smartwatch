package com.lost.domain.usecase

import com.lost.domain.port.TimeSource
import com.lost.domain.port.TripReader
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject

/** Días de esta semana (desde el lunes) en los que el usuario hizo algún recorrido. */
class GetWeekTripDaysUseCase @Inject constructor(
    private val tripReader: TripReader,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(zoneId: ZoneId = ZoneId.systemDefault()): Set<DayOfWeek> {
        val today = Instant.ofEpochMilli(timeSource.nowMillis()).atZone(zoneId).toLocalDate()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).atStartOfDay(zoneId)
        return tripReader.tripsSince(monday.toInstant().toEpochMilli())
            .mapNotNull { trip -> trip.startedAtMillis?.let { Instant.ofEpochMilli(it).atZone(zoneId).dayOfWeek } }
            .toSet()
    }
}
