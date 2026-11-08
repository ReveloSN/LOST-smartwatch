package com.lost.watch.ui.map

import com.lost.designsystem.tokens.ComponentTokens
import com.lost.domain.model.GeoPoint

/**
 * Las dos rutas de una pantalla de mapa (la de siempre y la de hoy) y cuánto margen dejar
 * al encuadrarlas (más en pantalla completa, donde hay textos encima; menos en un mapa pequeño).
 * Si hay [focus], se encuadran solo esos puntos ([MapFocus]); las rutas se dibujan enteras igual.
 */
data class MapRoutes(
    val habitual: List<GeoPoint>,
    val today: List<GeoPoint>,
    val marginFraction: Double = ComponentTokens.RealMap.FIT_MARGIN_FRACTION,
    val focus: List<GeoPoint>? = null
) {
    val isEmpty: Boolean get() = habitual.isEmpty() && today.isEmpty()

    /** Los puntos que tienen que caber en el mapa. */
    val framed: List<GeoPoint> get() = focus ?: (habitual + today)
}
