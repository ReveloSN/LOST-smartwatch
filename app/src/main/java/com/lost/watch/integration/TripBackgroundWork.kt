package com.lost.watch.integration

import com.lost.domain.session.AssistanceTimers
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Lo que corre junto al monitor mientras dura un recorrido: los efectos de cada transición (háptica,
 * métricas, aviso), los temporizadores de la escalada, lo que se dice a partir de la ubicación (perdida, giros
 * durante la guía) y la pregunta por notificación cuando la app no está a la vista.
 */
class TripBackgroundWork @Inject constructor(
    private val effects: AssistanceEffects,
    private val timers: AssistanceTimers,
    private val locationFeedback: LocationFeedback,
    private val promptEffect: AssistancePromptEffect
) {
    suspend fun run(): Unit = coroutineScope {
        launch { effects.run() }
        launch { timers.run() }
        launch { locationFeedback.run() }
        launch { promptEffect.run() }
    }
}
