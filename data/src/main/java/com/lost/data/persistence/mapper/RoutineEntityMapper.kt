package com.lost.data.persistence.mapper

import com.lost.data.persistence.codec.DayOfWeekSetCodec
import com.lost.data.persistence.codec.GeoPointListCodec
import com.lost.data.persistence.entity.RoutineEntity
import com.lost.domain.model.DurationStatistics
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Routine
import com.lost.domain.model.RoutineSchedule
import javax.inject.Inject

/** Convierte entre la fila de base de datos y el modelo de dominio [Routine]. */
class RoutineEntityMapper @Inject constructor() {
    fun toEntity(routine: Routine) = RoutineEntity(
        id = routine.id,
        name = routine.name,
        destinationKind = routine.destinationKind.name,
        encodedPath = GeoPointListCodec.encode(routine.path),
        days = DayOfWeekSetCodec.encode(routine.schedule.days),
        departureMinuteOfDay = routine.schedule.departureMinuteOfDay,
        durationCount = routine.duration.count,
        durationMeanMinutes = routine.duration.meanMinutes,
        durationM2 = routine.duration.m2,
        timesObserved = routine.timesObserved,
        isConfirmed = routine.isConfirmed
    )

    fun toDomain(entity: RoutineEntity) = Routine(
        id = entity.id,
        name = entity.name,
        destinationKind = PlaceKind.entries.firstOrNull { it.name == entity.destinationKind } ?: PlaceKind.OTHER,
        path = GeoPointListCodec.decode(entity.encodedPath),
        schedule = RoutineSchedule(DayOfWeekSetCodec.decode(entity.days), entity.departureMinuteOfDay),
        duration = DurationStatistics(entity.durationCount, entity.durationMeanMinutes, entity.durationM2),
        timesObserved = entity.timesObserved,
        isConfirmed = entity.isConfirmed
    )
}
