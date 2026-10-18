package com.lost.domain.usecase

import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.port.MetricsTracker
import com.lost.domain.port.TimeSource
import javax.inject.Inject

/** Registra un evento de métricas con la hora actual. */
class TrackMetricEventUseCase @Inject constructor(
    private val tracker: MetricsTracker,
    private val timeSource: TimeSource
) {
    suspend operator fun invoke(type: MetricEventType, elapsedMillis: Long? = null) {
        tracker.track(MetricEvent(type, timeSource.nowMillis(), elapsedMillis))
    }
}
