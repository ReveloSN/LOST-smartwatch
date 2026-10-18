package com.lost.domain.detection

import com.lost.domain.model.units.Meters

/**
 * Por qué LOST cree que algo no es normal. No lleva texto: la UI lo traduce
 * a lenguaje humano con strings.xml ("Pasaste 3 veces por la misma cuadra").
 */
sealed interface DeviationReason {
    data class LeftUsualRoute(val distance: Meters) : DeviationReason

    data class RepeatedArea(val times: Int) : DeviationReason

    data class TakingLonger(val extraMinutes: Int) : DeviationReason

    data object MovingAwayFromDestination : DeviationReason

    data object ErraticDirection : DeviationReason

    data object UnusualStop : DeviationReason
}
