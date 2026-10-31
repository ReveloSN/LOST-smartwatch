package com.lost.watch.ui.map

import com.lost.designsystem.tokens.ComponentTokens
import com.lost.domain.model.GeoPoint

/**
 * Las dos rutas de una pantalla de mapa (la de siempre y la de hoy) y cuánto margen dejar
 * al encuadrarlas (más en pantalla completa, donde hay textos encima; menos en un mapa pequeño).
 */
data class MapRoutes(
    val habitual: List<GeoPoint>,
    val today: List<GeoPoint>,
    val marginFraction: Double = ComponentTokens.RealMap.FIT_MARGIN_FRACTION
) {
    val isEmpty: Boolean get() = habitual.isEmpty() && today.isEmpty()
}
