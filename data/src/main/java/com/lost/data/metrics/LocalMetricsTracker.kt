package com.lost.data.metrics

import com.lost.data.persistence.dao.MetricEventDao
import com.lost.data.persistence.mapper.MetricEventEntityMapper
import com.lost.domain.metrics.MetricEvent
import com.lost.domain.port.MetricsReader
import com.lost.domain.port.MetricsTracker
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Guarda las métricas en Room, en el reloj. Nada sale del dispositivo. */
class LocalMetricsTracker @Inject constructor(
    private val dao: MetricEventDao,
    private val mapper: MetricEventEntityMapper
) : MetricsTracker,
    MetricsReader {
    override suspend fun track(event: MetricEvent) = dao.insert(mapper.toEntity(event))

    override fun observeEventsSince(epochMillis: Long): Flow<List<MetricEvent>> =
        dao.observeSince(epochMillis).map { rows -> rows.mapNotNull(mapper::toDomain) }
}
