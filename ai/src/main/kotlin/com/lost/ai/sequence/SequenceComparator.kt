package com.lost.ai.sequence

import com.lost.domain.model.GeoPoint

/**
 * Compara dos secuencias de puntos y devuelve el costo medio en metros (0 = idénticas).
 * Con `isPartial = true` la candidata es un recorrido en curso y se compara contra
 * el mejor prefijo de la referencia ("open-end").
 */
interface SequenceComparator {
    fun compare(reference: List<GeoPoint>, candidate: List<GeoPoint>, isPartial: Boolean): Double
}
