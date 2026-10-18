package com.lost.data.persistence

import com.lost.data.persistence.dao.RoutineDao
import com.lost.data.persistence.entity.RoutineEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

/** DAO en memoria: Room genera la implementación real, aquí basta un mapa. */
class FakeRoutineDao : RoutineDao {
    private val rows = MutableStateFlow<Map<String, RoutineEntity>>(emptyMap())

    override fun observeAll(): Flow<List<RoutineEntity>> = rows.map { it.values.toList() }

    override suspend fun getAll(): List<RoutineEntity> = rows.value.values.toList()

    override suspend fun getById(id: String): RoutineEntity? = rows.value[id]

    override suspend fun upsert(routine: RoutineEntity) {
        rows.value = rows.value + (routine.id to routine)
    }

    override suspend fun delete(id: String) {
        rows.value = rows.value - id
    }

    override suspend fun deleteAll() {
        rows.value = emptyMap()
    }
}
