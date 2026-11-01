package com.lost.domain.metrics

import com.lost.domain.detection.DeviationCause
import com.lost.domain.model.AlertPlace
import com.lost.domain.port.AlertPlaceRecorder
import com.lost.domain.port.TimeSource
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.session.TripSession
import javax.inject.Inject

/** Cada vez que LOST pregunta, guarda dónde estaba la persona y la causa principal (para 03.2). */
class AlertPlaceLogger @Inject constructor(
    private val session: TripSession,
    private val recorder: AlertPlaceRecorder,
    private val timeSource: TimeSource
) {
    suspend fun onTransition(transition: AssistanceTransition) {
        if (!transition.isNewAlert) return
        val snapshot = session.snapshot.value
        val location = snapshot.currentLocation ?: return
        val cause = snapshot.detection.reasons.firstOrNull()?.let(DeviationCause::of)
        recorder.record(AlertPlace(location, timeSource.nowMillis(), cause, snapshot.routine?.name))
    }
}
