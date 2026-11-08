package com.lost.watch.feature.demo

import com.lost.data.demo.DemoScenario

/**
 * Recorridos de demostración disponibles, cuál se está reproduciendo y cuántos días de la ruta de siempre
 * lleva aprendidos LOST ([learnedDays] de [daysToLearn]).
 */
data class DemoUiState(
    val scenarios: List<DemoScenario>,
    val playing: DemoScenario?,
    val learnedDays: Int = 0,
    val daysToLearn: Int = 0
) {
    val isRoutineLearned: Boolean get() = daysToLearn > 0 && learnedDays >= daysToLearn
}
