package com.lost.ai.fixtures

import com.lost.ai.geometry.LocalProjection
import com.lost.ai.geometry.PlanePoint
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import kotlin.math.ceil

/**
 * Genera recorridos sintéticos caminando en metros sobre un plano local
 * (x = este, y = norte) a 1.4 m/s, con un punto GPS cada 5 s.
 */
class SyntheticWalk(origin: GeoPoint = TripFixtures.ORIGIN, startMillis: Long = TripFixtures.MONDAY_6_40) {
    private val projection = LocalProjection(origin)
    private var position = PlanePoint(0.0, 0.0)
    private var time = startMillis
    private val points = mutableListOf(projection.toGeoPoint(position, time))

    fun to(x: Double, y: Double): SyntheticWalk {
        val target = PlanePoint(x, y)
        val distance = position.distanceTo(target)
        val steps = ceil(distance / STEP_METERS).toInt().coerceAtLeast(1)
        val start = position
        for (i in 1..steps) {
            val f = i.toDouble() / steps
            position = PlanePoint(start.x + (target.x - start.x) * f, start.y + (target.y - start.y) * f)
            time += (distance / steps / SPEED_MPS * 1000).toLong()
            points += projection.toGeoPoint(position, time)
        }
        return this
    }

    fun stay(seconds: Int): SyntheticWalk {
        repeat(seconds / SAMPLE_SECONDS) {
            time += SAMPLE_SECONDS * 1000L
            points += projection.toGeoPoint(position, time)
        }
        return this
    }

    /** Recorre un cuadrado desde la posición actual (para simular dar vueltas a la manzana). */
    fun square(side: Double, times: Int): SyntheticWalk {
        val (x, y) = position
        repeat(times) { to(x + side, y).to(x + side, y - side).to(x, y - side).to(x, y) }
        return this
    }

    fun points(): List<GeoPoint> = points.toList()

    fun trip(id: String = "today") = Trip(id, points())

    private companion object {
        const val SPEED_MPS = 1.4
        const val SAMPLE_SECONDS = 5
        const val STEP_METERS = SPEED_MPS * SAMPLE_SECONDS
    }
}
