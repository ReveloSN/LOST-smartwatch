package com.lost.data.persistence

import com.lost.data.persistence.dao.TripDao
import com.lost.data.persistence.mapper.TripEntityMapper
import com.lost.domain.model.Trip
import com.lost.domain.port.TripReader
import com.lost.domain.port.TripRecorder
import javax.inject.Inject

/** Repositorio de recorridos sobre Room (adaptador de los puertos del dominio). */
class RoomTripRepository @Inject constructor(private val dao: TripDao, private val mapper: TripEntityMapper) :
    TripRecorder,
    TripReader {
    override suspend fun record(trip: Trip) = dao.insert(mapper.toEntity(trip))

    override suspend fun deleteAll() = dao.deleteAll()

    override suspend fun tripsSince(epochMillis: Long): List<Trip> = dao.tripsSince(epochMillis).map(mapper::toDomain)
}
