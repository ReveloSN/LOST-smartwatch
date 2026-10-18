package com.lost.data.persistence.mapper

import com.lost.data.persistence.entity.MetricEventEntity
import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import javax.inject.Inject

/** Convierte eventos de métricas. Un tipo desconocido (de una versión vieja) se descarta. */
class MetricEventEntityMapper @Inject constructor() {
    fun toEntity(event: MetricEvent) = MetricEventEntity(
        type = event.type.name,
        timestampMillis = event.timestampMillis,
        elapsedMillis = event.elapsedMillis
    )

    fun toDomain(entity: MetricEventEntity): MetricEvent? {
        val type = MetricEventType.entries.firstOrNull { it.name == entity.type } ?: return null
        return MetricEvent(type, entity.timestampMillis, entity.elapsedMillis)
    }
}
