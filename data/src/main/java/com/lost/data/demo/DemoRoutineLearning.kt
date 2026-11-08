package com.lost.data.demo

import android.content.Context
import com.lost.data.R
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.port.TimeSource
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Demo de una persona nueva: LOST empieza sin rutinas y la aprende a la vista, un día de la semana
 * pasada por toque, con el mismo aprendizaje que usa con recorridos reales (`LearnRoutineFromTripUseCase`).
 * Al quinto día la rutina se confirma; la demo le pone el nombre que la persona le daría ("Casa → U").
 *
 * Contar los días y aprender el siguiente va bajo un candado: dos toques seguidos (o un recorrido elegido
 * mientras aprende) no deben enseñar dos veces el mismo día.
 */
@Singleton
class DemoRoutineLearning @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpx: DemoGpxLoader,
    private val learnRoutine: LearnRoutineFromTripUseCase,
    private val repositories: DemoRepositories,
    private val timeSource: TimeSource
) {
    private val weekTrips = DemoWeekTrips()
    private val lock = Mutex()

    /** Cuántos días de la ruta de siempre lleva aprendidos (0 a 5). */
    suspend fun learnedDays(): Int = lock.withLock { countLearnedDays() }

    suspend fun learnNextDay(): Int = lock.withLock {
        learnDay()
        countLearnedDays()
    }

    /** Los escenarios que comparan con la ruta de siempre la necesitan aprendida. */
    suspend fun learnWholeWeek() = lock.withLock {
        repeat(DemoWeekTrips.DAYS) { learnDay() }
    }

    private suspend fun learnDay() {
        val day = countLearnedDays()
        if (day >= DemoWeekTrips.DAYS) return
        val today = Instant.ofEpochMilli(timeSource.nowMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
        val learned = learnRoutine(weekTrips.trip(route(), day, today))
        if (learned != null && learned.isConfirmed && learned.name.isBlank()) nameAsHabitual(learned)
    }

    private suspend fun countLearnedDays(): Int =
        habitualRoutine()?.timesObserved?.coerceAtMost(DemoWeekTrips.DAYS) ?: 0

    /** La rutina de la demo es la que sale y llega donde la ruta de siempre del GPX, no otra que ya exista. */
    private suspend fun habitualRoutine(): Routine? {
        val route = route()
        return repositories.routineReader.routines()
            .filter { it.path.isNotEmpty() && isNear(it.path.first(), route.first()) }
            .filter { isNear(it.path.last(), route.last()) }
            .maxByOrNull { it.timesObserved }
    }

    private fun route(): List<GeoPoint> = gpx.points(DemoScenario.NORMAL_ROUTINE)

    private fun isNear(a: GeoPoint, b: GeoPoint): Boolean =
        abs(a.latitude - b.latitude) * METERS_PER_DEGREE <= SAME_PLACE_METERS &&
            abs(a.longitude - b.longitude) * METERS_PER_DEGREE <= SAME_PLACE_METERS

    private suspend fun nameAsHabitual(routine: Routine) {
        repositories.routineWriter.save(
            routine.copy(name = context.getString(R.string.demo_routine_name), destinationKind = PlaceKind.UNIVERSITY)
        )
    }

    private companion object {
        const val METERS_PER_DEGREE = 111_195.0
        const val SAME_PLACE_METERS = 150.0
    }
}
