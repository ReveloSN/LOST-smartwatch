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

    /** Giro para pasar de este rumbo a [other]: positivo a la derecha, negativo a la izquierda, en (−180, 180]. */
    fun signedDifferenceTo(other: Degrees): Degrees {
        val diff = (other.normalized().value - normalized().value + FULL_TURN) % FULL_TURN
        return Degrees(if (diff > HALF_TURN) diff - FULL_TURN else diff)
    }

    private companion object {
        const val FULL_TURN = 360.0
        const val HALF_TURN = 180.0
    }
}
