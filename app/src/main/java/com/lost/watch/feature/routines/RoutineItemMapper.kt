package com.lost.watch.feature.routines

import com.lost.domain.model.Routine
import javax.inject.Inject
import kotlin.math.roundToInt

/** Convierte una rutina del dominio en la fila que muestra la UI. */
class RoutineItemMapper @Inject constructor() {
    fun map(routine: Routine) = RoutineItem(
        id = routine.id,
        name = routine.name.takeIf { it.isNotBlank() },
        kind = routine.destinationKind,
        schedule = routine.schedule,
        durationMinutes = routine.duration.meanMinutes.roundToInt(),
        timesObserved = routine.timesObserved,
        isConfirmed = routine.isConfirmed
    )
}
