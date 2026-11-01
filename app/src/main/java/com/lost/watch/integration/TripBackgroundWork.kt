package com.lost.watch.integration

import com.lost.domain.session.AssistanceTimers
import com.lost.domain.session.LocationSignalWatchdog
import javax.inject.Inject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

/**
 * Lo que corre junto al monitor mientras dura un recorrido: los efectos de cada transición (háptica,
 * métricas, aviso), los temporizadores de la escalada y el aviso de ubicación perdida.
 */
class TripBackgroundWork @Inject constructor(
    private val effects: AssistanceEffects,
    private val timers: AssistanceTimers,
    private val locationWatchdog: LocationSignalWatchdog
) {
    suspend fun run(): Unit = coroutineScope {
        launch { effects.run() }
        launch { timers.run() }
        launch { locationWatchdog.run() }
    }
}
