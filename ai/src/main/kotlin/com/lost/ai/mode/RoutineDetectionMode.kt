package com.lost.ai.mode

import com.lost.ai.config.ScoringWeights
import com.lost.ai.learning.RoutineMatch
import com.lost.ai.scoring.CompositeAnomalyScorer
import com.lost.ai.sequence.RouteProgressLocator
import com.lost.ai.signals.SignalContext
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.model.GeoPoint

/** Modo rutina: compara el recorrido de hoy contra la ruta de siempre (camino, secuencia, tiempo y ritmo). */
class RoutineDetectionMode(
    private val match: RoutineMatch,
    private val scorer: CompositeAnomalyScorer,
    private val weights: ScoringWeights,
    private val progressLocator: RouteProgressLocator
) : DetectionMode {
    override val kind = DetectionModeKind.ROUTINE
    override val routineId: String = match.routine.id

    override fun evaluate(path: List<GeoPoint>, request: DetectionRequest): ModeEvaluation {
        val progress = path.lastOrNull()?.let { progressLocator.progress(it, match.path) } ?: 0.0
        val context = SignalContext(
            trip = request.trip,
            path = path,
            routine = match.routine,
            routinePath = match.path,
            target = request.destination ?: match.routine.destination,
            progress = progress,
            sensitivity = request.sensitivity
        )
        return ModeEvaluation(scorer.score(context, weights), progress)
    }
}
