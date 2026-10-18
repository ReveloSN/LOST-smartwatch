package com.lost.domain.usecase

import com.lost.domain.model.Routine
import com.lost.domain.port.RoutineReader
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Rutinas para "Mis recorridos": primero las confirmadas, luego las que se están aprendiendo. */
class ObserveRoutinesUseCase @Inject constructor(private val reader: RoutineReader) {
    operator fun invoke(): Flow<List<Routine>> = reader.observeRoutines().map { routines ->
        routines.sortedWith(ROUTINE_ORDER)
    }

    private companion object {
        val ROUTINE_ORDER = compareByDescending<Routine> { it.isConfirmed }
            .thenBy { it.schedule.departureMinuteOfDay }
    }
}
