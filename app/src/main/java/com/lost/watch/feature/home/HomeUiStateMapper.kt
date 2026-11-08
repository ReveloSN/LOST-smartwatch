package com.lost.watch.feature.home

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
            status = StatusPresentation.of(snapshot.detection.level),
            ringProgress = snapshot.detection.routeProgress.toFloat(),
            routineName = routine?.name?.takeIf { it.isNotBlank() } ?: snapshot.destination?.name,
            arrivalMinuteOfDay = startedAt?.takeIf { routine != null }?.let { minuteOfDay(it, expected, zoneId) },
            isNewPlace = snapshot.detection.mode == DetectionModeKind.NEW_PLACE,
            isVoluntaryDetour = snapshot.detection.isVoluntaryDetour,
            isDemoAvailable = isDemoAvailable
        )
    }

    private fun minuteOfDay(startMillis: Long, durationMinutes: Int, zoneId: ZoneId): Int {
        val arrival = Instant.ofEpochMilli(startMillis).atZone(zoneId).plusMinutes(durationMinutes.toLong())
        return arrival.hour * MINUTES_PER_HOUR + arrival.minute
    }

    private companion object {
        const val MINUTES_PER_HOUR = 60
    }
}
