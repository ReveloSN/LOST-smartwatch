package com.lost.watch.feature.demo

import com.lost.data.demo.DemoScenario

/** Acciones del modo demo. */
sealed interface DemoUiEvent {
    data class Play(val scenario: DemoScenario) : DemoUiEvent

    data object Stop : DemoUiEvent
}
