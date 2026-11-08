package com.lost.domain.port

import com.lost.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

/** Fuente de ubicaciones en tiempo real (GPS del reloj o un GPX simulado). */
interface LocationSource {
    fun locations(): Flow<GeoPoint>

    /**
     * Ubicaciones con un intervalo que cambia según lo que pasa (GPS adaptativo, ver
     * `LocationCadencePolicy`). Una fuente que no puede cambiar su ritmo (el GPX de la demo) lo ignora.
     */
    fun locations(intervalMillis: Flow<Long>): Flow<GeoPoint> = locations()
}
