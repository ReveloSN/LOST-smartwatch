package com.lost.data.persistence.mapper

import com.lost.data.persistence.entity.AlertPlaceEntity
import com.lost.domain.detection.DeviationCause
import com.lost.domain.model.AlertPlace
import com.lost.domain.model.GeoPoint
import javax.inject.Inject

/** Convierte lugares de alerta. Una causa desconocida (de una versión vieja) se guarda como "sin causa". */
class AlertPlaceEntityMapper @Inject constructor() {
    fun toEntity(place: AlertPlace) = AlertPlaceEntity(
        timestampMillis = place.timestampMillis,
        latitude = place.location.latitude,
        longitude = place.location.longitude,
        cause = place.cause?.name,
        routineName = place.routineName
    )

    fun toDomain(entity: AlertPlaceEntity) = AlertPlace(
        location = GeoPoint(entity.latitude, entity.longitude, entity.timestampMillis),
        timestampMillis = entity.timestampMillis,
        cause = DeviationCause.entries.firstOrNull { it.name == entity.cause },
        routineName = entity.routineName
    )
}
