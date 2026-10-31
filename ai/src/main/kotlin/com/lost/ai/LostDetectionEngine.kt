package com.lost.ai

import com.lost.ai.explanation.DeviationExplanationBuilder
import com.lost.ai.geometry.IncrementalTrajectoryResampler
import com.lost.ai.mode.DetectionModeFactory
import com.lost.ai.scoring.AssistanceLevelClassifier
import com.lost.ai.scoring.VoluntaryDetourDetector
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.detection.DetectionResult
import com.lost.domain.port.TripEvaluator

/**
 * Fachada del motor de IA: `evaluate(request)` y nada más. Por dentro remuestrea (de forma
 * incremental: cada punto nuevo solo procesa el último tramo),
 * elige el modo, combina señales, clasifica el nivel, detecta desvíos voluntarios y explica por qué.
 */
class LostDetectionEngine(
    private val resampler: IncrementalTrajectoryResampler,
    private val modeFactory: DetectionModeFactory,
    private val classifier: AssistanceLevelClassifier,
    private val voluntaryDetector: VoluntaryDetourDetector,
    private val explanationBuilder: DeviationExplanationBuilder
) : TripEvaluator {

    override fun evaluate(request: DetectionRequest): DetectionResult {
        val path = resampler.resample(request.trip.points)
        val mode = modeFactory.create(path, request)
        val evaluation = mode.evaluate(path, request)
        val score = evaluation.score
        val level = classifier.classify(score.probability, request.thresholds)
        return DetectionResult(
            probability = score.probability,
            level = level,
            reasons = explanationBuilder.build(score.readings),
            isVoluntaryDetour = level == AssistanceLevel.LOW && voluntaryDetector.isVoluntary(score),
            mode = mode.kind,
            routineId = mode.routineId,
            routeProgress = evaluation.progress
        )
    }
}
