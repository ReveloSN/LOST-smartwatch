package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import com.lost.domain.port.LocationSource
import com.lost.domain.port.TimeSource
import javax.inject.Inject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow

/**
 * Reproduce un GPX como si fuera el GPS. Acelera la reproducción (`speedFactor`) pero
 * conserva la separación real entre tiempos, así el motor ve duraciones realistas.
 */
class SimulatedLocationSource @Inject constructor(
    private val opener: GpxAssetOpener,
    private val reader: GpxRouteReader,
    private val selector: DemoScenarioSelector,
    private val timeSource: TimeSource
) : LocationSource {
    var speedFactor: Double = DEFAULT_SPEED_FACTOR

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun locations(): Flow<GeoPoint> = selector.current.flatMapLatest { scenario ->
        if (scenario == null) emptyFlow() else replay(scenario)
    }

    private fun replay(scenario: DemoScenario): Flow<GeoPoint> = flow {
        val points = opener.open(scenario.assetName).use(reader::read)
        val first = points.firstOrNull() ?: return@flow
        val sessionStart = timeSource.nowMillis()
        points.forEachIndexed { index, point ->
            if (index > 0) delay(((point.timestampMillis - points[index - 1].timestampMillis) / speedFactor).toLong())
            emit(point.copy(timestampMillis = sessionStart + point.timestampMillis - first.timestampMillis))
        }
    }

    private companion object {
        const val DEFAULT_SPEED_FACTOR = 10.0
    }
}
