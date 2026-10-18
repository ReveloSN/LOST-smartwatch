package com.lost.data.persistence.mapper

import com.lost.data.persistence.entity.DestinationEntity
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import javax.inject.Inject

/** Convierte entre la fila de base de datos y el modelo de dominio [Destination]. */
class DestinationEntityMapper @Inject constructor() {
    fun toEntity(destination: Destination) = DestinationEntity(
        id = destination.id,
        name = destination.name,
        latitude = destination.location.latitude,
        longitude = destination.location.longitude,
        kind = destination.kind.name,
        lastUsedAtMillis = destination.lastUsedAtMillis
    )

    fun toDomain(entity: DestinationEntity) = Destination(
        id = entity.id,
        name = entity.name,
        location = GeoPoint(entity.latitude, entity.longitude),
        kind = PlaceKind.entries.firstOrNull { it.name == entity.kind } ?: PlaceKind.OTHER,
        lastUsedAtMillis = entity.lastUsedAtMillis
    )
}
