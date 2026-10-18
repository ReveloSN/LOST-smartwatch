package com.lost.ai.learning

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Routine

/** Rutina candidata con su ruta remuestreada y el costo de compararla (menor = más parecida). */
data class RoutineMatch(val routine: Routine, val path: List<GeoPoint>, val cost: Double) : Comparable<RoutineMatch> {
    override fun compareTo(other: RoutineMatch): Int = cost.compareTo(other.cost)
}
