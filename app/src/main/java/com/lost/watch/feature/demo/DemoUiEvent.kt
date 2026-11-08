package com.lost.watch.feature.demo

import com.lost.data.demo.DemoScenario

/** Acciones del modo demo. */
sealed interface DemoUiEvent {
    data class Play(val scenario: DemoScenario) : DemoUiEvent

    data object Stop : DemoUiEvent

    /** Enseña a LOST un día más de la ruta de siempre (la demo de una persona nueva). */
    data object LearnNextDay : DemoUiEvent
}
