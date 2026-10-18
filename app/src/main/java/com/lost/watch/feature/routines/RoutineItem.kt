package com.lost.watch.feature.routines

import com.lost.domain.model.PlaceKind
import com.lost.domain.model.RoutineSchedule

/** Una fila de "Mis recorridos". Sin nombre todavía = "Nuevo recorrido". */
data class RoutineItem(
    val id: String,
    val name: String?,
    val kind: PlaceKind,
    val schedule: RoutineSchedule,
    val durationMinutes: Int,
    val timesObserved: Int,
    val isConfirmed: Boolean
)
