package com.lost.domain.usecase

import com.lost.domain.port.RoutineWriter
import javax.inject.Inject

/** "Borrar": elimina una rutina aprendida. */
class DeleteRoutineUseCase @Inject constructor(private val writer: RoutineWriter) {
    suspend operator fun invoke(routineId: String) = writer.delete(routineId)
}
