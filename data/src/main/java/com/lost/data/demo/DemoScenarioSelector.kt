package com.lost.data.demo

import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Qué recorrido de demo se está reproduciendo (o ninguno). */
@Singleton
class DemoScenarioSelector @Inject constructor() {
    private val selected = MutableStateFlow<DemoScenario?>(null)
    val current: StateFlow<DemoScenario?> = selected.asStateFlow()

    fun play(scenario: DemoScenario) {
        selected.value = scenario
    }

    fun stop() {
        selected.value = null
    }
}
