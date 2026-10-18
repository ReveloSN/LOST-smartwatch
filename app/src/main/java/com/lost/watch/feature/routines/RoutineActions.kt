package com.lost.watch.feature.routines

import com.lost.domain.usecase.ConfirmRoutineUseCase
import com.lost.domain.usecase.DeleteRoutineUseCase
import com.lost.domain.usecase.RenameRoutineUseCase
import javax.inject.Inject

/** "Tú controlas lo que aprende": guardar, renombrar y borrar, agrupados para el detalle. */
class RoutineActions @Inject constructor(
    private val confirmRoutine: ConfirmRoutineUseCase,
    private val renameRoutine: RenameRoutineUseCase,
    private val deleteRoutine: DeleteRoutineUseCase
) {
    suspend fun confirm(routineId: String) = confirmRoutine(routineId)

    suspend fun rename(routineId: String, newName: String) = renameRoutine(routineId, newName)

    suspend fun delete(routineId: String) = deleteRoutine(routineId)
}
