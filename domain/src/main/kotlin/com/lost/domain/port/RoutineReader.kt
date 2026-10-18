package com.lost.domain.port

import com.lost.domain.model.Routine
import kotlinx.coroutines.flow.Flow

/** Lectura de rutinas aprendidas. */
interface RoutineReader {
    fun observeRoutines(): Flow<List<Routine>>

    suspend fun routines(): List<Routine>

    suspend fun routine(id: String): Routine?
}
