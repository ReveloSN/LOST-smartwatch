package com.lost.domain.usecase

import com.lost.domain.metrics.InsightsCalculator
import com.lost.domain.metrics.InsightsSummary
import com.lost.domain.port.MetricsReader
import com.lost.domain.port.TimeSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** Métricas de los últimos 7 días, recalculadas cada vez que llega un evento nuevo. */
class ObserveWeeklyInsightsUseCase @Inject constructor(
    private val metricsReader: MetricsReader,
    private val timeSource: TimeSource,
    private val calculator: InsightsCalculator
) {
    operator fun invoke(): Flow<InsightsSummary> {
        val weekAgo = timeSource.nowMillis() - TimeUnit.DAYS.toMillis(DAYS_IN_WEEK)
        return metricsReader.observeEventsSince(weekAgo).map(calculator::calculate)
    }

    private companion object {
        const val DAYS_IN_WEEK = 7L
    }
}
