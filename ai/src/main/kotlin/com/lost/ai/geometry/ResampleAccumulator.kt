package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import kotlin.math.sqrt

/**
 * El paso a paso del remuestreo: se le dan los puntos en orden y va dejando un punto cada vez que la
 * persona está a `spacingMeters` **en línea recta** del último punto dejado. Medir en línea recta (y no
 * a lo largo del rastro) hace que el temblor del GPS no sume metros ni invente giros; antes, cada punto
 * se promedia con los de los últimos segundos ([GpsSmoothing]). Un punto aislado que implicaría ir más
 * rápido que `jumpSpeedMps` se toma como un salto de la señal y se descarta.
 * Guarda su estado, así se pueden seguir agregando puntos después (remuestreo incremental).
 */
internal class ResampleAccumulator(
    private val spacingMeters: Double,
    private val gps: GpsSmoothing,
    private val distanceCalculator: HaversineDistanceCalculator,
    first: GeoPoint
) {
    private val committed = mutableListOf(first)
    private val recent = ArrayDeque<GeoPoint>().apply { addLast(first) }
    private var lastAccepted = first
    private var lastSmoothed = first
    private var isPreviousRejected = false

    fun add(point: GeoPoint) {
        // Nunca se descartan dos seguidos: si la persona de verdad va rápido, el rastro no se queda atascado.
        if (!isPreviousRejected && isJump(point)) {
            isPreviousRejected = true
            return
        }
        isPreviousRejected = false
        lastAccepted = point
        val smoothed = smooth(point)
        var start = lastSmoothed
        lastSmoothed = smoothed
        while (distance(committed.last(), smoothed) >= spacingMeters) {
            val next = pointAtSpacing(committed.last(), start, smoothed)
            committed += next
            start = next
        }
    }

    /**
     * Lo remuestreado hasta ahora. Al final va el último punto real (no el promedio): para saber dónde
     * está la persona ahora importa más no llegar tarde que el temblor de un solo punto.
     */
    fun result(): List<GeoPoint> = if (distance(committed.last(), lastAccepted) >
        spacingMeters / 2
    ) {
        committed + lastAccepted
    } else {
        committed.toList()
    }

    private fun isJump(point: GeoPoint): Boolean {
        val seconds = (point.timestampMillis - lastAccepted.timestampMillis) / MILLIS_PER_SECOND
        return seconds > 0 && distance(lastAccepted, point) / seconds > gps.jumpSpeedMps
    }

    /** Promedio de los puntos de los últimos segundos. Sin hora creciente (datos sintéticos) no promedia. */
    private fun smooth(point: GeoPoint): GeoPoint {
        if (point.timestampMillis <= recent.last().timestampMillis) recent.clear()
        recent.addLast(point)
        val windowMillis = (gps.smoothingSeconds * MILLIS_PER_SECOND).toLong()
        while (point.timestampMillis - recent.first().timestampMillis > windowMillis) recent.removeFirst()
        return GeoPoint(
            recent.sumOf {
                it.latitude
            } / recent.size,
            recent.sumOf { it.longitude } / recent.size,
            point.timestampMillis
        )
    }

    /** El punto del tramo `from → to` que queda justo a `spacingMeters` de `anchor` (from está más cerca). */
    private fun pointAtSpacing(anchor: GeoPoint, from: GeoPoint, to: GeoPoint): GeoPoint {
        val projection = LocalProjection(anchor)
        val start = projection.toMeters(from)
        val end = projection.toMeters(to)
        val dx = end.x - start.x
        val dy = end.y - start.y
        // |start + t·(end − start)| = spacing, con la fórmula de "medio b": t = (−h + √(h² − a·c)) / a.
        val a = dx * dx + dy * dy
        val h = start.x * dx + start.y * dy
        val c = start.x * start.x + start.y * start.y - spacingMeters * spacingMeters
        val fraction = if (a == 0.0) 1.0 else ((-h + sqrt(h * h - a * c)) / a).coerceIn(0.0, 1.0)
        return GeoPoint(
            latitude = from.latitude + (to.latitude - from.latitude) * fraction,
            longitude = from.longitude + (to.longitude - from.longitude) * fraction,
            timestampMillis = from.timestampMillis + ((to.timestampMillis - from.timestampMillis) * fraction).toLong()
        )
    }

    private fun distance(a: GeoPoint, b: GeoPoint) = distanceCalculator.distance(a, b).value

    private companion object {
        const val MILLIS_PER_SECOND = 1000.0
    }
}
