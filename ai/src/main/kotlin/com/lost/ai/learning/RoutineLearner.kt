package com.lost.ai.learning

import com.lost.ai.geometry.TrajectoryResampler
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import com.lost.domain.model.Trip
import com.lost.domain.port.IdGenerator
import com.lost.domain.port.RoutineLearningEngine
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.EnumSet
import java.util.concurrent.TimeUnit

/**
 * Reconocimiento de patrones: agrupa recorridos parecidos en rutinas.
 * Si el recorrido coincide con una rutina, la refuerza (veces, días, hora, duración con Welford);
 * si no, empieza una rutina nueva que se confirmará al repetirse.
 */
class RoutineLearner(
    private val resampler: TrajectoryResampler,
    private val matcher: RoutineMatcher,
    private val statistics: RunningStatistics,
    private val confirmationPolicy: RoutineConfirmationPolicy,
    private val idGenerator: IdGenerator,
    private val zoneId: ZoneId = ZoneId.systemDefault()
) : RoutineLearningEngine {

    override fun learn(trip: Trip, knownRoutines: List<Routine>): Routine? {
        val path = resampler.resample(trip.points)
        if (path.size < 2) return null
        val match = matcher.bestCompletedMatch(path, knownRoutines)
        return match?.let { reinforce(it.routine, trip) } ?: create(trip, path)
    }

    private fun reinforce(routine: Routine, trip: Trip): Routine {
        val start = startOf(trip)
        val times = routine.timesObserved + 1
        val averagedDeparture =
            (routine.schedule.departureMinuteOfDay * routine.timesObserved + minuteOfDay(start)) / times
        return routine.copy(
            schedule = RoutineSchedule(routine.schedule.days + start.dayOfWeek, averagedDeparture),
            duration = statistics.add(routine.duration, durationMinutes(trip)),
            timesObserved = times,
            isConfirmed = routine.isConfirmed || confirmationPolicy.isConfirmed(times)
        )
    }

    private fun create(trip: Trip, path: List<com.lost.domain.model.GeoPoint>): Routine {
        val start = startOf(trip)
        return Routine(
            id = idGenerator.newId(),
            name = "",
            destinationKind = PlaceKind.OTHER,
            path = path,
            schedule = RoutineSchedule(EnumSet.of(start.dayOfWeek), minuteOfDay(start)),
            duration = statistics.add(DurationStatistics.EMPTY, durationMinutes(trip)),
            timesObserved = 1,
            isConfirmed = confirmationPolicy.isConfirmed(1)
        )
    }

    private fun startOf(trip: Trip): ZonedDateTime = Instant.ofEpochMilli(trip.startedAtMillis ?: 0L).atZone(zoneId)

    private fun minuteOfDay(time: ZonedDateTime) = time.hour * MINUTES_PER_HOUR + time.minute

    private fun durationMinutes(trip: Trip) = trip.durationMillis.toDouble() / TimeUnit.MINUTES.toMillis(1)

    private companion object {
        const val MINUTES_PER_HOUR = 60
    }
}
