package com.lost.domain.usecase

import com.lost.domain.metrics.MetricEventType
import com.lost.domain.metrics.WeekActivity
import com.lost.domain.port.MetricsReader
import com.lost.domain.port.TimeSource
import com.lost.domain.port.TripReader
import java.time.DayOfWeek
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters
import javax.inject.Inject
import kotlinx.coroutines.flow.first

/**
 * Días de esta semana (desde el lunes) con algún recorrido y días con desvío. Un día cuenta como
 * "con desvío" si LOST mostró al menos una alerta, porque es lo que el usuario vivió como desvío.
 */
class GetWeekActivityUseCase @Inject constructor(
    private val tripReader: TripReader,
    private val metricsReader: MetricsReader,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(zoneId: ZoneId = ZoneId.systemDefault()): WeekActivity {
        val mondayMillis = startOfWeekMillis(zoneId)
        val tripDays = tripReader.tripsSince(mondayMillis).mapNotNull { it.startedAtMillis }
        val alertDays = metricsReader.observeEventsSince(mondayMillis).first()
            .filter { it.type == MetricEventType.ALERT_SHOWN }
            .map { it.timestampMillis }
        return WeekActivity(
            daysWithTrips = tripDays.daysOfWeek(zoneId),
            daysWithDeviation = alertDays.daysOfWeek(zoneId)
        )
    }

    private fun startOfWeekMillis(zoneId: ZoneId): Long {
        val today = Instant.ofEpochMilli(timeSource.nowMillis()).atZone(zoneId).toLocalDate()
        val monday = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
        return monday.atStartOfDay(zoneId).toInstant().toEpochMilli()
    }

    private fun List<Long>.daysOfWeek(zoneId: ZoneId): Set<DayOfWeek> =
        mapTo(mutableSetOf()) { Instant.ofEpochMilli(it).atZone(zoneId).dayOfWeek }
}
