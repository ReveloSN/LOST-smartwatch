package com.lost.domain.usecase

import com.lost.domain.error.LostResult
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.WalkingRouteService
import javax.inject.Inject

/** Ruta a pie hacia un destino de "lugar nuevo". Solo se llama cuando el usuario elige ese destino. */
class FetchWalkingRouteUseCase @Inject constructor(private val service: WalkingRouteService) {
    suspend operator fun invoke(from: GeoPoint, to: GeoPoint): LostResult<List<GeoPoint>> =
        service.walkingRoute(from, to)
}
