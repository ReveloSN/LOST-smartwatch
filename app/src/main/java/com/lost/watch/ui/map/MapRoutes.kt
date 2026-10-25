package com.lost.watch.ui.map

import com.lost.domain.model.GeoPoint

/** Las dos rutas de una pantalla de mapa (la de siempre y la de hoy). */
data class MapRoutes(val habitual: List<GeoPoint>, val today: List<GeoPoint>) {
    val isEmpty: Boolean get() = habitual.isEmpty() && today.isEmpty()
}
