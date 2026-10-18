package com.lost.domain.usecase

import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import javax.inject.Inject

/** "Renombrar": cambia el nombre visible de una rutina. Ignora nombres vacíos. */
class RenameRoutineUseCase @Inject constructor(private val reader: RoutineReader, private val writer: RoutineWriter) {
    suspend operator fun invoke(routineId: String, newName: String) {
        val name = newName.trim()
        if (name.isEmpty()) return
        val routine = reader.routine(routineId) ?: return
        writer.save(routine.copy(name = name))
    }
}
