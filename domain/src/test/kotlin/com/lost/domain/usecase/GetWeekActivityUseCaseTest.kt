package com.lost.domain.usecase

import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import com.lost.domain.port.MetricsReader
import com.lost.domain.port.TripReader
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetWeekActivityUseCaseTest {
    private val zone = ZoneOffset.UTC
    private val wednesdayNoon = millisAt(2026, 10, 28, 12)
    private val monday = millisAt(2026, 10, 26, 7)
    private val tuesday = millisAt(2026, 10, 27, 7)
    private val lastFriday = millisAt(2026, 10, 23, 7)

    @Test
    fun `marca los dias con recorrido y los dias con alerta`() = runTest {
        val trips = listOf(trip(monday), trip(tuesday))
        val events = listOf(
            MetricEvent(MetricEventType.ALERT_SHOWN, tuesday + MINUTE),
            MetricEvent(MetricEventType.USER_RESPONDED, monday + MINUTE)
        )
        val activity = useCase(trips, events)(zone)
        assertEquals(setOf(DayOfWeek.MONDAY, DayOfWeek.TUESDAY), activity.daysWithTrips)
        assertEquals(setOf(DayOfWeek.TUESDAY), activity.daysWithDeviation)
    }

    @Test
    fun `solo cuenta desde el lunes de esta semana`() = runTest {
        val reader = RecordingTripReader(emptyList())
        val metrics = RecordingMetricsReader(emptyList())
        GetWeekActivityUseCase(reader, metrics) { wednesdayNoon }(zone)
        assertEquals(millisAt(2026, 10, 26, 0), reader.since)
        assertEquals(millisAt(2026, 10, 26, 0), metrics.since)
    }

    @Test
    fun `una alerta de la semana pasada no marca el dia`() = runTest {
        val events = listOf(MetricEvent(MetricEventType.ALERT_SHOWN, lastFriday))
        val activity = useCase(emptyList(), events)(zone)
        assertEquals(emptySet<DayOfWeek>(), activity.daysWithDeviation)
    }

    private fun useCase(trips: List<Trip>, events: List<MetricEvent>) =
        GetWeekActivityUseCase(RecordingTripReader(trips), RecordingMetricsReader(events)) { wednesdayNoon }

    private fun trip(startMillis: Long) = Trip("t$startMillis", listOf(GeoPoint(4.6, -74.08, startMillis)))

    private fun millisAt(year: Int, month: Int, day: Int, hour: Int) =
        LocalDateTime.of(year, month, day, hour, 0).toInstant(ZoneOffset.UTC).toEpochMilli()

    private class RecordingTripReader(private val trips: List<Trip>) : TripReader {
        var since: Long? = null

        override suspend fun tripsSince(epochMillis: Long): List<Trip> {
            since = epochMillis
            return trips.filter { (it.startedAtMillis ?: 0L) >= epochMillis }
        }
    }

    private class RecordingMetricsReader(private val events: List<MetricEvent>) : MetricsReader {
        var since: Long? = null

        override fun observeEventsSince(epochMillis: Long): Flow<List<MetricEvent>> {
            since = epochMillis
            return flowOf(events.filter { it.timestampMillis >= epochMillis })
        }
    }

    private companion object {
        const val MINUTE = 60_000L
    }
}
