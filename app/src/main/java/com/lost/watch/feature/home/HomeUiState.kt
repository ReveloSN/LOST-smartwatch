package com.lost.watch.feature.home

import com.lost.watch.ui.StatusPresentation

/**
 * Inicio: estado tranquilo o recorrido en curso.
 * @property arrivalMinuteOfDay hora estimada de llegada (minuto del día) si hay rutina.
 */
data class HomeUiState(
    val isMonitoring: Boolean,
    val status: StatusPresentation,
    val ringProgress: Float,
    val routineName: String?,
    val arrivalMinuteOfDay: Int?,
    val isNewPlace: Boolean,
    val isVoluntaryDetour: Boolean,
    val isDemoAvailable: Boolean
) {
    companion object {
        fun idle(isDemoAvailable: Boolean) = HomeUiState(
            isMonitoring = false,
            status = StatusPresentation.ON_ROUTE,
            ringProgress = 0f,
            routineName = null,
            arrivalMinuteOfDay = null,
            isNewPlace = false,
            isVoluntaryDetour = false,
            isDemoAvailable = isDemoAvailable
        )
    }
}
