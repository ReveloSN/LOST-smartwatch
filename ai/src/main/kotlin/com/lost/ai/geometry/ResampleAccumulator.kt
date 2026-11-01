package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import kotlin.math.sqrt

/**
 * El paso a paso del remuestreo: se le dan los puntos en orden y va dejando un punto cada vez que la
 * persona está a `spacingMeters` **en línea recta** del último punto dejado. Medir en línea recta (y no
 * a lo largo del rastro) hace que el temblor del GPS no sume metros ni invente giros; antes, cada punto
 * se promedia con los de los últimos segundos ([GpsWindowAverage]). Un punto aislado que implicaría ir más
 * rápido que `jumpSpeedMps` se toma como un salto de la señal y se descarta, y uno cuya precisión
 * reportada es peor que `maxAccuracyMeters` se ignora siempre ([GpsSmoothing.isPrecise]).
 * Guarda su estado, así se pueden seguir agregando puntos después (remuestreo incremental).
 */
internal class ResampleAccumulator(
    private val spacingMeters: Double,
    private val gps: GpsSmoothing,
    private val distanceCalculator: HaversineDistanceCalculator,
    first: GeoPoint
) {
    private val committed = mutableListOf(first)
    private val average = GpsWindowAverage(gps).apply {
        if (gps.isPrecise(first)) add(first)
    }
    private var lastAccepted = first
    private var lastSmoothed = first
    private var isPreviousRejected = false

    // Si el recorrido empezó con un punto impreciso, el primer punto preciso ocupa su lugar.
    private var isSeedImprecise = !gps.isPrecise(first)

    fun add(point: GeoPoint) {
        if (!gps.isPrecise(point)) return
        if (isSeedImprecise) {
            reseed(point)
            return
        }
        // Nunca se descartan dos seguidos: si la persona de verdad va rápido, el rastro no se queda atascado.
        if (!isPreviousRejected && isJump(point)) {
            isPreviousRejected = true
            return
        }
        isPreviousRejected = false
        lastAccepted = point
        val smoothed = average.add(point)
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

    private fun reseed(point: GeoPoint) {
        isSeedImprecise = false
        committed.clear()
        committed += point
        average.add(point)
        lastAccepted = point
        lastSmoothed = point
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
