package com.lost.data.demo

import android.content.Context
import com.lost.data.R
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.port.TimeSource
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import dagger.hilt.android.qualifiers.ApplicationContext
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject

/**
 * Demo de una persona nueva: LOST empieza sin rutinas y la aprende a la vista, un día de la semana
 * pasada por toque, con el mismo aprendizaje que usa con recorridos reales (`LearnRoutineFromTripUseCase`).
 * Al quinto día la rutina se confirma; la demo le pone el nombre que la persona le daría ("Casa → U").
 */
class DemoRoutineLearning @Inject constructor(
    @ApplicationContext private val context: Context,
    private val gpx: DemoGpxLoader,
    private val learnRoutine: LearnRoutineFromTripUseCase,
    private val repositories: DemoRepositories,
    private val timeSource: TimeSource
) {
    private val weekTrips = DemoWeekTrips()

    /** Cuántos días de la ruta de siempre lleva aprendidos (0 a 5). */
    suspend fun learnedDays(): Int = habitualRoutine()?.timesObserved?.coerceAtMost(DemoWeekTrips.DAYS) ?: 0

    suspend fun learnNextDay(): Int {
        val day = learnedDays()
        if (day >= DemoWeekTrips.DAYS) return day
        val today = Instant.ofEpochMilli(timeSource.nowMillis()).atZone(ZoneId.systemDefault()).toLocalDate()
        val learned = learnRoutine(weekTrips.trip(gpx.points(DemoScenario.NORMAL_ROUTINE), day, today))
        if (learned != null && learned.isConfirmed && learned.name.isBlank()) nameAsHabitual(learned)
        return learnedDays()
    }

    /** Los escenarios que comparan con la ruta de siempre la necesitan aprendida. */
    suspend fun learnWholeWeek() {
        repeat(DemoWeekTrips.DAYS) { learnNextDay() }
    }

    private suspend fun habitualRoutine(): Routine? =
        repositories.routineReader.routines().maxByOrNull { it.timesObserved }

    private suspend fun nameAsHabitual(routine: Routine) {
        repositories.routineWriter.save(
            routine.copy(name = context.getString(R.string.demo_routine_name), destinationKind = PlaceKind.UNIVERSITY)
        )
    }
}
