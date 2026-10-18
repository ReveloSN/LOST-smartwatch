package com.lost.data.persistence

import com.lost.data.persistence.dao.DestinationDao
import com.lost.data.persistence.mapper.DestinationEntityMapper
import com.lost.domain.model.Destination
import com.lost.domain.port.DestinationReader
import com.lost.domain.port.DestinationWriter
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Repositorio de destinos sobre Room. */
class RoomDestinationRepository @Inject constructor(
    private val dao: DestinationDao,
    private val mapper: DestinationEntityMapper
) : DestinationReader,
    DestinationWriter {
    override fun observeDestinations(): Flow<List<Destination>> =
        dao.observeAll().map { rows -> rows.map(mapper::toDomain) }

    override suspend fun save(destination: Destination) = dao.upsert(mapper.toEntity(destination))
}
