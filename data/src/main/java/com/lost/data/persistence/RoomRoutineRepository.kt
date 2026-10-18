package com.lost.data.persistence

import com.lost.data.persistence.dao.RoutineDao
import com.lost.data.persistence.mapper.RoutineEntityMapper
import com.lost.domain.model.Routine
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repositorio de rutinas sobre Room. */
class RoomRoutineRepository @Inject constructor(private val dao: RoutineDao, private val mapper: RoutineEntityMapper) :
    RoutineReader,
    RoutineWriter {
    override fun observeRoutines(): Flow<List<Routine>> = dao.observeAll().map { rows -> rows.map(mapper::toDomain) }

    override suspend fun routines(): List<Routine> = dao.getAll().map(mapper::toDomain)

    override suspend fun routine(id: String): Routine? = dao.getById(id)?.let(mapper::toDomain)

    override suspend fun save(routine: Routine) = dao.upsert(mapper.toEntity(routine))

    override suspend fun delete(id: String) = dao.delete(id)

    override suspend fun deleteAll() = dao.deleteAll()
}
