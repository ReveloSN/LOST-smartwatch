package com.lost.domain.model

/**
 * Rutina: recorrido que se repite con camino, horario, duración y ritmo parecidos.
 * Su [path] es la trayectoria de referencia contra la que se compara el recorrido de hoy.
 */
data class Routine(
    val id: String,
    val name: String,
    val destinationKind: PlaceKind,
    val path: List<GeoPoint>,
    val schedule: RoutineSchedule,
    val duration: DurationStatistics,
    val timesObserved: Int,
    val isConfirmed: Boolean
) {
    val destination: GeoPoint? get() = path.lastOrNull()
}
