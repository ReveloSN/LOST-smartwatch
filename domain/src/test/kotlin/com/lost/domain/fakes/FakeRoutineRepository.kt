package com.lost.domain.fakes

import com.lost.domain.model.Routine
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeRoutineRepository(initial: List<Routine> = emptyList()) :
    RoutineReader,
    RoutineWriter {
    private val state = MutableStateFlow(initial.associateBy { it.id })

    override fun observeRoutines(): Flow<List<Routine>> = state.map { it.values.toList() }

    override suspend fun routines(): List<Routine> = state.value.values.toList()

    override suspend fun routine(id: String): Routine? = state.value[id]

    override suspend fun save(routine: Routine) {
        state.value = state.value + (routine.id to routine)
    }

    override suspend fun delete(id: String) {
        state.value = state.value - id
    }

    override suspend fun deleteAll() {
        state.value = emptyMap()
    }
}
