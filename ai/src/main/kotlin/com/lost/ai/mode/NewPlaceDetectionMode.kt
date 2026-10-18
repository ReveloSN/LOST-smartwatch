package com.lost.ai.mode

import com.lost.ai.config.ScoringWeights
import com.lost.ai.scoring.CompositeAnomalyScorer
import com.lost.ai.signals.SignalContext
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.model.GeoPoint

/**
 * Modo lugar nuevo (hospital, aeropuerto): no hay ruta de referencia, así que solo
 * busca señales generales: vueltas, regresos al mismo punto y alejarse del destino marcado.
 */
class NewPlaceDetectionMode(private val scorer: CompositeAnomalyScorer, private val weights: ScoringWeights) :
    DetectionMode {
    override val kind = DetectionModeKind.NEW_PLACE
    override val routineId: String? = null

    override fun evaluate(path: List<GeoPoint>, request: DetectionRequest): ModeEvaluation {
        val context = SignalContext(
            trip = request.trip,
            path = path,
            routine = null,
            routinePath = emptyList(),
            target = request.destination,
            progress = 0.0,
            sensitivity = request.sensitivity
        )
        return ModeEvaluation(scorer.score(context, weights), progress = 0.0)
    }
}
