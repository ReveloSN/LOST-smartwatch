package com.lost.ai.geometry

import com.lost.domain.model.GeoPoint
import kotlin.math.floor

/**
 * Grilla espacial: cada punto cae en una celda. Guardar los índices por celda en un
 * `HashMap<GridCell, MutableList<Int>>` hace O(1) la pregunta "¿ya pasé por aquí?".
 */
class SpatialGrid(origin: GeoPoint, private val cellMeters: Double) {
    private val projection = LocalProjection(origin)
    private val visits = HashMap<GridCell, MutableList<Int>>()

    fun cellOf(point: GeoPoint): GridCell {
        val plane = projection.toMeters(point)
        return GridCell(floor(plane.x / cellMeters).toInt(), floor(plane.y / cellMeters).toInt())
    }

    /** Registra el punto y devuelve los índices anteriores que cayeron en la misma celda. */
    fun visit(index: Int, point: GeoPoint): List<Int> {
        val indices = visits.getOrPut(cellOf(point)) { mutableListOf() }
        val previous = indices.toList()
        indices += index
        return previous
    }
}
