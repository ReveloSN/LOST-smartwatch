package com.lost.data.demo

import com.lost.domain.detection.DeviationCause
import com.lost.domain.model.AlertPlace
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.TimeSource
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * En modo demo deja algo que mostrar en "Donde más te desorientas" (03.2), coherente con la historia de
 * Sebastián: dos veces dio vueltas por la calle cerrada de su ruta y una vez se desorientó cerca de la clínica.
 * Todo pasó hace más de una semana, así no contradice "Tu semana: sin desvíos". Si ya hay lugares guardados
 * (por ejemplo, de alertas reales de la demo) no agrega nada.
 */
class DemoAlertPlacesSeeder @Inject constructor(
    private val store: DemoAlertPlaceStore,
    private val routines: RoutineReader,
    private val timeSource: TimeSource,
    private val gpx: DemoGpxLoader
) {
    suspend fun seedIfNeeded(isDemo: Boolean = DemoMode.isEnabled) {
        val now = timeSource.nowMillis()
        if (!isDemo || store.reader.placesSince(now - days(LOOK_BACK_DAYS)).isNotEmpty()) return
        val routine = routines.routines().firstOrNull()?.name
        val closedStreet = gpx.points(DemoScenario.DISORIENTATION)
        val clinic = gpx.points(DemoScenario.NEW_PLACE)
        // Un GPX vacío o dañado no debe impedir que la app arranque: simplemente no se siembra.
        if (closedStreet.isEmpty() || clinic.isEmpty()) return
        listOf(
            AlertPlace(
                at(closedStreet, LOOPS_SHARE),
                now - days(FIRST_LOOP_DAYS_AGO),
                DeviationCause.REPEATED_AREA,
                routine
            ),
            AlertPlace(
                at(closedStreet, LOOPS_SHARE_AGAIN),
                now - days(SECOND_LOOP_DAYS_AGO),
                DeviationCause.REPEATED_AREA,
                routine
            ),
            AlertPlace(at(clinic, CLINIC_SHARE), now - days(CLINIC_DAYS_AGO), DeviationCause.ERRATIC_DIRECTION, null)
        ).forEach { store.recorder.record(it) }
    }

    /** El punto del recorrido a esa fracción del camino. */
    private fun at(points: List<GeoPoint>, share: Double): GeoPoint = points[((points.size - 1) * share).toInt()]

    private fun days(count: Long) = TimeUnit.DAYS.toMillis(count)

    private companion object {
        const val LOOK_BACK_DAYS = 30L
        const val LOOPS_SHARE = 0.6
        const val LOOPS_SHARE_AGAIN = 0.61
        const val CLINIC_SHARE = 0.7
        const val FIRST_LOOP_DAYS_AGO = 9L
        const val SECOND_LOOP_DAYS_AGO = 12L
        const val CLINIC_DAYS_AGO = 16L
    }
}
