package com.lost.domain.session

import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.HapticNotifier
import com.lost.domain.port.TimeSource
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

/**
 * Avisa cuando, durante un recorrido, pasa [MonitoringThresholds.LOCATION_LOST_MILLIS] sin una ubicación confiable:
 * no llega ningún punto o solo llegan puntos que el mismo GPS marca como imprecisos. Mide con la hora en que
 * llegan los puntos (no con la del GPS), así funciona igual con la demo. Al perder la ubicación vibra una vez,
 * suave: LOST no puede ayudar sin saber dónde está la persona y debe decirlo. Después de llegar al destino
 * ya no avisa (por ejemplo, cuando termina el GPX de la demo).
 */
class LocationSignalWatchdog @Inject constructor(
    private val session: TripSession,
    private val timeSource: TimeSource,
    private val hapticNotifier: HapticNotifier
) {
    suspend fun run(): Unit = coroutineScope {
        val lastReliableAt = AtomicLong(timeSource.nowMillis())
        launch {
            session.snapshot.map { it.currentLocation }.distinctUntilChanged().filterNotNull().collect { point ->
                if (isReliable(point)) {
                    lastReliableAt.set(timeSource.nowMillis())
                    publish(isWeak = false)
                }
            }
        }
        while (isActive) {
            delay(MonitoringThresholds.LOCATION_CHECK_MILLIS)
            val isLost = timeSource.nowMillis() - lastReliableAt.get() > MonitoringThresholds.LOCATION_LOST_MILLIS
            publish(isWeak = isLost && !session.snapshot.value.hasArrived)
        }
    }

    private fun publish(isWeak: Boolean) {
        if (session.snapshot.value.hasWeakLocation == isWeak) return
        session.publish { it.copy(hasWeakLocation = isWeak) }
        if (isWeak) hapticNotifier.play(HapticPattern.GENTLE)
    }

    private fun isReliable(point: GeoPoint): Boolean =
        point.accuracyMeters?.let { it <= MonitoringThresholds.WEAK_LOCATION_ACCURACY_METERS } ?: true
}
