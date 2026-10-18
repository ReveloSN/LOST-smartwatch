package com.lost.domain.model.units

import kotlin.math.abs

/** Ángulo en grados (rumbos y diferencias de dirección). */
@JvmInline
value class Degrees(val value: Double) {
    /** Lleva el ángulo al rango [0, 360). */
    fun normalized(): Degrees = Degrees(((value % FULL_TURN) + FULL_TURN) % FULL_TURN)

    /** Diferencia angular más corta entre dos rumbos, siempre entre 0 y 180. */
    fun shortestDifferenceTo(other: Degrees): Degrees {
        val diff = abs(normalized().value - other.normalized().value)
        return Degrees(if (diff > HALF_TURN) FULL_TURN - diff else diff)
    }

    private companion object {
        const val FULL_TURN = 360.0
        const val HALF_TURN = 180.0
    }
}
