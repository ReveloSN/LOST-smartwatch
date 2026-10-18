package com.lost.domain.port

import com.lost.domain.metrics.MetricEvent
import kotlinx.coroutines.flow.Flow

/** Lee los eventos de métricas guardados. */
interface MetricsReader {
    fun observeEventsSince(epochMillis: Long): Flow<List<MetricEvent>>
}
