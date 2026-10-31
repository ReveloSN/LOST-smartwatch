package com.lost.domain.port

import com.lost.domain.error.LostResult
import com.lost.domain.model.GeoPoint

/** Calcula una ruta a pie por calles reales entre dos puntos (se envían solo origen y destino). */
fun interface WalkingRouteService {
    suspend fun walkingRoute(from: GeoPoint, to: GeoPoint): LostResult<List<GeoPoint>>
}
