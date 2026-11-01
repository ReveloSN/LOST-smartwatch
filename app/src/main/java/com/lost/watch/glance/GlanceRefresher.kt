package com.lost.watch.glance

import com.lost.domain.session.TripSession
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * Mantiene al día la complicación y la vista rápida (patrón Observer): cuando cambia algo que se ve en ellas
 * (estado, destino, llegada o un décimo del avance del anillo), les pide redibujarse. Pedirlo en cada
 * punto GPS gastaría batería sin cambiar nada en pantalla. Al arrancar el proceso también pide redibujar,
 * por si quedó mostrando un recorrido que ya no existe.
 */
class GlanceRefresher @Inject constructor(
    private val session: TripSession,
    private val mapper: GlanceSummaryMapper,
    private val surfaces: GlanceSurfaces
) {
    suspend fun run() {
        session.snapshot
            .map { visibleKey(mapper.map(it)) }
            .distinctUntilChanged()
            .collect { surfaces.requestUpdate() }
    }

    private fun visibleKey(summary: GlanceSummary) =
        summary.copy(progress = (summary.progress * PROGRESS_STEPS).roundToInt().toFloat() / PROGRESS_STEPS)

    private companion object {
        const val PROGRESS_STEPS = 10
    }
}
