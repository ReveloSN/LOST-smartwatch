package com.lost.domain.port

import com.lost.domain.metrics.MetricEvent

/** Registra eventos de uso para medir eficacia, eficiencia y falsos positivos. */
interface MetricsTracker {
    suspend fun track(event: MetricEvent)
}
