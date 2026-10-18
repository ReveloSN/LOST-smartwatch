package com.lost.domain.session

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.units.Degrees
import com.lost.domain.model.units.Meters

/** Hacia dónde guiar: punto objetivo, distancia y rumbo absoluto desde la posición actual. */
data class Guidance(val target: GeoPoint, val distance: Meters, val bearing: Degrees)
