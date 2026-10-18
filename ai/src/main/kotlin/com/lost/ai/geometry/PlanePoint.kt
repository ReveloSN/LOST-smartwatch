package com.lost.ai.geometry

import kotlin.math.hypot

/** Punto en metros sobre el plano local (x = este, y = norte). */
data class PlanePoint(val x: Double, val y: Double) {
    fun distanceTo(other: PlanePoint): Double = hypot(x - other.x, y - other.y)
}
