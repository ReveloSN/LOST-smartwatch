package com.lost.data.persistence.mapper

import com.lost.data.persistence.codec.GeoPointListCodec
import com.lost.data.persistence.entity.TripEntity
import com.lost.domain.model.Trip
import javax.inject.Inject

/** Convierte entre la fila de base de datos y el modelo de dominio [Trip]. */
class TripEntityMapper @Inject constructor() {
    fun toEntity(trip: Trip) = TripEntity(
        id = trip.id,
        startedAtMillis = trip.startedAtMillis ?: 0L,
        encodedPoints = GeoPointListCodec.encode(trip.points)
    )

    fun toDomain(entity: TripEntity) = Trip(id = entity.id, points = GeoPointListCodec.decode(entity.encodedPoints))
}
