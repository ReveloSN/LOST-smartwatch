package com.lost.watch.feature.routines

import com.lost.domain.model.GeoPoint

/** Detalle de un recorrido: datos, trayectoria de referencia y si el borrado está armado. */
data class RoutineDetailUiState(
    val item: RoutineItem?,
    val path: List<GeoPoint>,
    val isDeleteArmed: Boolean,
    val isDeleted: Boolean
)
