package com.lost.domain.port

import com.lost.domain.model.GeoPoint
import kotlinx.coroutines.flow.Flow

/** Fuente de ubicaciones en tiempo real (GPS del reloj o un GPX simulado). */
interface LocationSource {
    fun locations(): Flow<GeoPoint>
}
