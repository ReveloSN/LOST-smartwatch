package com.lost.domain.session

import com.lost.domain.assistance.AssistanceState
import com.lost.domain.detection.DetectionResult
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Routine
import com.lost.domain.model.Trip

/**
 * Foto del recorrido en curso que observan Inicio, Monitoreo y la capa de alerta.
 * @property routine rutina contra la que se compara (null en lugar nuevo o sin recorrido).
 * @property guidance a dónde volver (punto más cercano de la ruta de siempre o el destino).
 */
data class TripSessionSnapshot(
    val isMonitoring: Boolean,
    val trip: Trip?,
    val detection: DetectionResult,
    val assistance: AssistanceState,
    val routine: Routine?,
    val destination: Destination?,
    val guidance: Guidance?,
    val hasArrived: Boolean
) {
    val currentLocation: GeoPoint? get() = trip?.lastPoint

    companion object {
        val IDLE = TripSessionSnapshot(
            isMonitoring = false,
            trip = null,
            detection = DetectionResult.CALM,
            assistance = AssistanceState.Calm,
            routine = null,
            destination = null,
            guidance = null,
            hasArrived = false
        )
    }
}
