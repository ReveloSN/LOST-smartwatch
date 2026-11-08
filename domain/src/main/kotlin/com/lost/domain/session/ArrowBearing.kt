package com.lost.domain.session

/**
 * Rumbo de la flecha de guía en pantalla. Con brújula, la flecha se gira según hacia dónde mira
 * el usuario ("sigue de frente" apunta arriba); sin brújula, apunta al rumbo absoluto (norte arriba).
 */
object ArrowBearing {
    private const val FULL_TURN = 360.0

    fun onScreen(bearingToTarget: Double, heading: Double?): Double {
        val relative = heading?.let { bearingToTarget - it } ?: bearingToTarget
        return ((relative % FULL_TURN) + FULL_TURN) % FULL_TURN
    }
}
