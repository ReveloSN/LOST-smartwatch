package com.lost.watch.feature.monitoring

import com.lost.domain.detection.DeviationReason
import com.lost.watch.ui.ProjectedRoutes
import com.lost.watch.ui.StatusPresentation

/** D · Recorrido de hoy vs. ruta de siempre, con lo que la IA nota. */
data class MonitoringUiState(
    val headline: MonitoringHeadline,
    val status: StatusPresentation,
    val probabilityFraction: Float,
    val routes: ProjectedRoutes,
    val reasons: List<DeviationReason>
) {
    companion object {
        val IDLE = MonitoringUiState(
            headline = MonitoringHeadline.IDLE,
            status = StatusPresentation.ON_ROUTE,
            probabilityFraction = 0f,
            routes = ProjectedRoutes(emptyList(), emptyList(), null),
            reasons = emptyList()
        )
    }
}
