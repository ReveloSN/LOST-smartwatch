package com.lost.watch.feature.home

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.AssistanceTimings
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.ui.StatusPresentation
import java.time.Instant
import java.time.ZoneId
import javax.inject.Inject
import kotlin.math.roundToInt

/** Convierte la foto del recorrido en el estado de Inicio (lógica pura, probada sin Android). */
class HomeUiStateMapper @Inject constructor() {
    fun map(
        snapshot: TripSessionSnapshot,
        isDemoAvailable: Boolean,
        zoneId: ZoneId = ZoneId.systemDefault()
    ): HomeUiState {
        if (!snapshot.isMonitoring) return HomeUiState.idle(isDemoAvailable)
        val routine = snapshot.routine
        val expected = routine?.duration?.meanMinutes?.roundToInt() ?: 0
        val startedAt = snapshot.trip?.startedAtMillis
        return HomeUiState(
            isMonitoring = true,
            status = statusOf(snapshot),
            ringProgress = snapshot.detection.routeProgress.toFloat(),
            routineName = routine?.name?.takeIf { it.isNotBlank() } ?: snapshot.destination?.name,
            arrivalMinuteOfDay = startedAt?.takeIf { routine != null }?.let { minuteOfDay(it, expected, zoneId) },
            // Tras "Voy a otro lado" el motor evalúa como lugar nuevo, pero para el usuario sigue siendo su desvío.
            isNewPlace = snapshot.detection.mode == DetectionModeKind.NEW_PLACE && !snapshot.isGoingElsewhere,
            isVoluntaryDetour = snapshot.detection.isVoluntaryDetour || snapshot.isGoingElsewhere,
            isDemoAvailable = isDemoAvailable,
            hasWeakLocation = snapshot.hasWeakLocation,
            pauseMinutes = PAUSE_MINUTES.takeIf { snapshot.assistance == AssistanceState.Paused }
        )
    }

    /**
     * Tras "Estoy bien" LOST no pregunta en 2 minutos; mostrar "Creo que necesitas ayuda" en ese rato
     * contradiría la respuesta. El estado se queda como mucho en "Posible desorientación": honesto, sin alarmar.
     */
    private fun statusOf(snapshot: TripSessionSnapshot): StatusPresentation {
        val status = StatusPresentation.of(snapshot.detection.level)
        val isPaused = snapshot.assistance == AssistanceState.Paused
        return if (isPaused && status == StatusPresentation.HELP) StatusPresentation.DEVIATION else status
    }

    private fun minuteOfDay(startMillis: Long, durationMinutes: Int, zoneId: ZoneId): Int {
        val arrival = Instant.ofEpochMilli(startMillis).atZone(zoneId).plusMinutes(durationMinutes.toLong())
        return arrival.hour * MINUTES_PER_HOUR + arrival.minute
    }

    private companion object {
        const val MINUTES_PER_HOUR = 60
        const val PAUSE_MINUTES = AssistanceTimings.PAUSE_AFTER_IM_FINE_SECONDS / MINUTES_PER_HOUR
    }
}
