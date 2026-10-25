package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint

/**
 * El paso a paso del remuestreo: se le dan los tramos en orden y va dejando un punto cada
 * `spacingMeters`. Guarda lo que sobra del último tramo, así se puede seguir agregando tramos
 * después (remuestreo incremental) sin repetir el trabajo ya hecho.
 */
internal class ResampleAccumulator(
    private val spacingMeters: Double,
    private val distanceCalculator: HaversineDistanceCalculator,
    first: GeoPoint
) {
    private val committed = mutableListOf(first)
    private var carried = 0.0

    fun add(from: GeoPoint, to: GeoPoint) {
        val segment = distanceCalculator.distance(from, to).value
        var position = spacingMeters - carried
        while (position <= segment) {
            committed += interpolate(from, to, position / segment)
            position += spacingMeters
        }
        carried = (carried + segment) % spacingMeters
    }

    /** Lo remuestreado hasta ahora; el último punto real se agrega si quedó más de medio tramo. */
    fun result(last: GeoPoint): List<GeoPoint> =
        if (carried > spacingMeters / 2) committed + last else committed.toList()

    private fun interpolate(from: GeoPoint, to: GeoPoint, fraction: Double) = GeoPoint(
        latitude = from.latitude + (to.latitude - from.latitude) * fraction,
        longitude = from.longitude + (to.longitude - from.longitude) * fraction,
        timestampMillis = from.timestampMillis + ((to.timestampMillis - from.timestampMillis) * fraction).toLong()
    )
}
