package com.lost.watch.feature.demo

import com.lost.data.demo.DemoScenario

/** Recorridos de demostración disponibles y cuál se está reproduciendo. */
data class DemoUiState(val scenarios: List<DemoScenario>, val playing: DemoScenario?)
