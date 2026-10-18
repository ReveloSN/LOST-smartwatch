package com.lost.domain.port

import com.lost.domain.model.Routine
import com.lost.domain.model.Trip

/** Aprende rutinas a partir de un recorrido terminado. Devuelve la rutina creada o actualizada. */
fun interface RoutineLearningEngine {
    fun learn(trip: Trip, knownRoutines: List<Routine>): Routine?
}
