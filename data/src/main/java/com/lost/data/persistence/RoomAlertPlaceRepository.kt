package com.lost.data.persistence

import com.lost.data.persistence.dao.AlertPlaceDao
import com.lost.data.persistence.mapper.AlertPlaceEntityMapper
import com.lost.domain.model.AlertPlace
import com.lost.domain.port.AlertPlaceReader
import com.lost.domain.port.AlertPlaceRecorder
import javax.inject.Inject

/** Patrón Repository: los lugares donde LOST preguntó, guardados solo en el reloj. */
class RoomAlertPlaceRepository @Inject constructor(
    private val dao: AlertPlaceDao,
    private val mapper: AlertPlaceEntityMapper
) : AlertPlaceRecorder,
    AlertPlaceReader {
    override suspend fun record(place: AlertPlace) = dao.insert(mapper.toEntity(place))

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun placesSince(epochMillis: Long): List<AlertPlace> = dao.since(epochMillis).map(mapper::toDomain)
}
