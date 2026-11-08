package com.lost.watch.feature.monitoring

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DeviationReason
import com.lost.domain.session.TripSessionSnapshot
import com.lost.watch.ui.RouteProjector
import com.lost.watch.ui.StatusPresentation
import javax.inject.Inject

/** Arma el estado del monitoreo a partir de la foto del recorrido (lógica pura, sin Android). */
class MonitoringUiStateMapper @Inject constructor() {
    private val projector = RouteProjector()

    fun map(snapshot: TripSessionSnapshot): MonitoringUiState {
        if (!snapshot.isMonitoring) return MonitoringUiState.IDLE
        val detection = snapshot.detection
        return MonitoringUiState(
            headline = headlineFor(snapshot),
            status = StatusPresentation.of(detection.level),
            routes = projector.project(snapshot.routine?.path.orEmpty(), snapshot.trip?.points.orEmpty()),
            reasons = detection.reasons
        )
    }

    private fun headlineFor(snapshot: TripSessionSnapshot): MonitoringHeadline {
        val detection = snapshot.detection
        return when {
            detection.mode == DetectionModeKind.NEW_PLACE -> MonitoringHeadline.NEW_PLACE

            detection.level != AssistanceLevel.LOW -> MonitoringHeadline.NOT_SIMILAR

            detection.reasons.any { it is DeviationReason.LeftUsualRoute } && !detection.isVoluntaryDetour ->
                MonitoringHeadline.DRIFTING

            detection.reasons.isNotEmpty() -> MonitoringHeadline.OTHER_STREET

            else -> MonitoringHeadline.ON_ROUTE
        }
    }
}
