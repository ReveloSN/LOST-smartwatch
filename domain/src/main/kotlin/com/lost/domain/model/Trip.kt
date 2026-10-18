package com.lost.domain.model

/**
 * Recorrido: secuencia ordenada de puntos GPS de un origen a un destino.
 * Un recorrido en curso todavía no tiene destino alcanzado.
 */
data class Trip(val id: String, val points: List<GeoPoint>) {
    val startedAtMillis: Long? get() = points.firstOrNull()?.timestampMillis
    val lastPoint: GeoPoint? get() = points.lastOrNull()
    val hasEnoughPoints: Boolean get() = points.size >= MIN_POINTS_TO_ANALYZE

    val durationMillis: Long
        get() = if (points.size < 2) 0L else points.last().timestampMillis - points.first().timestampMillis

    fun plus(point: GeoPoint): Trip = copy(points = points + point)

    companion object {
        const val MIN_POINTS_TO_ANALYZE = 3
    }
}
