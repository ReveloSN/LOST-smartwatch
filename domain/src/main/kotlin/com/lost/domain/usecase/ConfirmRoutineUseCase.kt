package com.lost.domain.usecase

import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import javax.inject.Inject

/** "Guardar": el usuario confirma un recorrido nuevo como rutina. */
class ConfirmRoutineUseCase @Inject constructor(private val reader: RoutineReader, private val writer: RoutineWriter) {
    suspend operator fun invoke(routineId: String) {
        val routine = reader.routine(routineId) ?: return
        writer.save(routine.copy(isConfirmed = true))
    }
}
