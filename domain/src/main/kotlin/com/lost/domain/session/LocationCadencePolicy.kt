package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind

/**
 * GPS adaptativo (batería): cada cuánto pedir la ubicación según lo que está pasando.
 * En la rutina y en calma basta un punto cada 15 s (≈ 20 m caminando, el paso del remuestreo);
 * si algo no cuadra se mira cada 5 s, y mientras LOST pregunta o guía, cada 2 s.
 */
object LocationCadencePolicy {
    fun intervalFor(snapshot: TripSessionSnapshot): Long = when {
        snapshot.assistance.isActive() -> MonitoringThresholds.GPS_GUIDING_MILLIS
        snapshot.isUnremarkable() -> MonitoringThresholds.GPS_RELAXED_MILLIS
        else -> MonitoringThresholds.GPS_ATTENTIVE_MILLIS
    }

    private fun AssistanceState.isActive(): Boolean =
        this != AssistanceState.Calm && this !is AssistanceState.SoftAsk && this !is AssistanceState.Acknowledged

    private fun TripSessionSnapshot.isUnremarkable(): Boolean = assistance == AssistanceState.Calm &&
        routine != null &&
        detection.mode == DetectionModeKind.ROUTINE &&
        detection.level == AssistanceLevel.LOW &&
        detection.reasons.isEmpty()
}
