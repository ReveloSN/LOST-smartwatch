package com.lost.ai.sequence

import com.lost.domain.model.GeoPoint

/**
 * Filas del DTW ya calculadas para un recorrido en curso. El recorrido solo crece y sus puntos remuestreados
 * no cambian (salvo el último, que es provisional), así que la siguiente comparación contra la misma ruta
 * puede retomar desde aquí en vez de rehacer la matriz entera.
 *
 * Guarda una entrada por ruta de referencia (comparada por identidad: la misma lista) para que tener
 * varias rutinas no haga que se pisen entre sí.
 */
internal class DtwRowCache(private val capacity: Int) {
    class Rows(val count: Int, val first: GeoPoint, val last: GeoPoint, val band: Int, val row: DoubleArray)

    private val entries = ArrayDeque<Pair<List<GeoPoint>, Rows>>()

    /** Las filas guardadas si [candidate] continúa el mismo recorrido con la misma banda; si no, `null`. */
    fun resume(reference: List<GeoPoint>, candidate: List<GeoPoint>, band: Int): Rows? {
        val rows = entries.firstOrNull { it.first === reference }?.second ?: return null
        val isContinuation = rows.band == band && candidate.size - 1 >= rows.count &&
            candidate.first() == rows.first && candidate[rows.count - 1] == rows.last
        return rows.takeIf { isContinuation }
    }

    fun save(reference: List<GeoPoint>, rows: Rows) {
        entries.removeAll { it.first === reference }
        if (entries.size >= capacity) entries.removeFirst()
        entries.addLast(reference to rows)
    }
}
