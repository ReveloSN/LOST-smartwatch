package com.lost.watch.feature.demo

import com.lost.data.demo.DemoRoutineLearning
import com.lost.data.demo.DemoScenario
import com.lost.data.demo.DemoScenarioSelector
import com.lost.data.di.ApplicationScope
import com.lost.domain.usecase.ObserveDestinationsUseCase
import com.lost.watch.integration.MonitoringController
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Pone en marcha un recorrido de la demo. Vive en el alcance de la aplicación y no en el ViewModel: al elegir
 * un recorrido se abre el monitoreo y la pantalla de la demo se cierra, y eso no debe cancelar a medias el
 * aprendizaje de la semana ni el arranque del recorrido.
 */
@Singleton
class DemoPlayer @Inject constructor(
    private val selector: DemoScenarioSelector,
    private val observeDestinations: ObserveDestinationsUseCase,
    private val monitoringController: MonitoringController,
    private val routineLearning: DemoRoutineLearning,
    @param:ApplicationScope private val scope: CoroutineScope
) {
    fun play(scenario: DemoScenario) {
        scope.launch {
            selector.stop()
            // Los escenarios de la ruta de siempre se comparan con ella: si aún no la aprendió, aprende la semana.
            if (scenario != DemoScenario.NEW_PLACE) routineLearning.learnWholeWeek()
            val destination = if (scenario == DemoScenario.NEW_PLACE) {
                observeDestinations().first().firstOrNull { it.id == HOSPITAL_DESTINATION_ID }
            } else {
                null
            }
            monitoringController.start(destination)
            selector.play(scenario)
        }
    }

    fun stop() {
        selector.stop()
        monitoringController.stop()
    }

    private companion object {
        const val HOSPITAL_DESTINATION_ID = "demo-hospital"
    }
}
