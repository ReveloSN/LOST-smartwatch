package com.lost.domain.fakes

import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.port.MetricsTracker

class RecordingMetricsTracker : MetricsTracker {
    val events = mutableListOf<MetricEvent>()
    val types: List<MetricEventType> get() = events.map { it.type }

    override suspend fun track(event: MetricEvent) {
        events += event
    }
}
