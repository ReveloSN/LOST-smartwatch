package com.lost.domain.port

import com.lost.domain.model.Routine

/** Escritura de rutinas aprendidas. */
interface RoutineWriter {
    suspend fun save(routine: Routine)

    suspend fun delete(id: String)

    suspend fun deleteAll()
}
