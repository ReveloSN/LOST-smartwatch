package com.lost.domain.port

import com.lost.domain.model.GeoPoint
import com.lost.domain.session.Guidance

/** Calcula hacia dónde guiar: al punto más cercano de la ruta de siempre o al destino. */
fun interface GuidanceCalculator {
    fun guide(from: GeoPoint, routePath: List<GeoPoint>, destination: GeoPoint?): Guidance?
}
