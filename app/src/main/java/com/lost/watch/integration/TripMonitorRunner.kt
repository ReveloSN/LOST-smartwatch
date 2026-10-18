package com.lost.watch.integration

import com.lost.data.di.ApplicationScope
import com.lost.domain.port.LocationSource
import com.lost.domain.session.AssistanceTimers
import com.lost.domain.session.TripMonitor
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Corre la tubería completa mientras dura un recorrido:
 * ubicación → `TripMonitor` (motor + máquina de estados) → efectos (háptica, métricas, aviso) + temporizadores.
 * Vive en el alcance de la aplicación, así sigue funcionando con la pantalla apagada.
 */
@Singleton
class TripMonitorRunner @Inject constructor(
    @ApplicationScope private val scope: CoroutineScope,
    private val locationSource: LocationSource,
    private val monitor: TripMonitor,
    private val timers: AssistanceTimers,
    private val effects: AssistanceEffects
) {
    private var job: Job? = null

    fun start() {
        job?.cancel()
        job = scope.launch {
            launch { effects.run() }
            launch { timers.run() }
            monitor.run(locationSource.locations())
        }
    }

    /** Detiene la tubería y deja que la IA aprenda del recorrido terminado. */
    fun stop(onFinished: () -> Unit) {
        job?.cancel()
        job = null
        scope.launch {
            monitor.finish()
            onFinished()
        }
    }
}
