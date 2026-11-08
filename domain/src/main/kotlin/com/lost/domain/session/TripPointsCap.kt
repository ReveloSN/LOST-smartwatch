package com.lost.domain.session

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip

/**
 * Tope de memoria del recorrido en curso: al llegar a [MonitoringThresholds.MAX_TRIP_POINTS]
 * se adelgaza la mitad más vieja (uno de cada dos puntos). Lo reciente, que es lo que más
 * importa para detectar vueltas y paradas, queda completo.
 */
object TripPointsCap {
    fun append(trip: Trip, point: GeoPoint, maxPoints: Int = MonitoringThresholds.MAX_TRIP_POINTS): Trip {
        if (trip.points.size < maxPoints) return trip.plus(point)
        val half = trip.points.size / 2
        val older = trip.points.take(half).filterIndexed { index, _ -> index % 2 == 0 }
        return trip.copy(points = older + trip.points.drop(half) + point)
    }
}
