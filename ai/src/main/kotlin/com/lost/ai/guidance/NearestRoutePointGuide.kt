package com.lost.ai.guidance

import com.lost.ai.geometry.BearingCalculator
import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.session.Guidance

/**
 * "Volver a tu ruta de siempre": guía al punto más cercano de la ruta conocida.
 * Sin ruta (lugar nuevo) guía directo al destino marcado.
 */
class NearestRoutePointGuide(
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator(),
    private val bearingCalculator: BearingCalculator = BearingCalculator()
) : GuidanceCalculator {
    override fun guide(from: GeoPoint, routePath: List<GeoPoint>, destination: GeoPoint?): Guidance? {
        val target = routePath.minByOrNull { distanceCalculator.distance(from, it).value } ?: destination
        return target?.let {
            Guidance(
                target = it,
                distance = distanceCalculator.distance(from, it),
                bearing = bearingCalculator.bearing(from, it)
            )
        }
    }
}
