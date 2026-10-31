package com.lost.ai

import com.lost.ai.config.DetectionConfig
import com.lost.ai.explanation.DeviationExplanationBuilder
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.ai.guidance.NearestRoutePointGuide
import com.lost.ai.learning.RoutineConfirmationPolicy
import com.lost.ai.learning.RoutineLearner
import com.lost.ai.learning.RoutineMatcher
import com.lost.ai.learning.RunningStatistics
import com.lost.ai.mode.DetectionModeFactory
import com.lost.ai.scoring.AssistanceLevelClassifier
import com.lost.ai.scoring.CompositeAnomalyScorer
import com.lost.ai.scoring.VoluntaryDetourDetector
import com.lost.ai.sequence.DtwSequenceComparator
import com.lost.ai.signals.DelaySignal
import com.lost.ai.signals.DeviationSignal
import com.lost.ai.signals.ErraticHeadingSignal
import com.lost.ai.signals.LoopSignal
import com.lost.ai.signals.MovingAwaySignal
import com.lost.ai.signals.OffRouteSignal
import com.lost.ai.signals.SequenceDivergenceSignal
import com.lost.ai.signals.UnusualStopSignal
import com.lost.domain.port.IdGenerator

/**
 * Arma las piezas del motor a partir de una configuración. El módulo `ai` es Kotlin puro,
 * así que no depende de Hilt: la app llama a estas funciones desde su módulo de DI.
 */
class LostEngineAssembly(private val config: DetectionConfig = DetectionConfig.DEFAULT) {
    private val thresholds = config.thresholds
    private val resampler = TrajectoryResampler(thresholds.resampleSpacingMeters)
    private val comparator = DtwSequenceComparator(thresholds.dtwBandRatio, thresholds.dtwMinBandPoints)
    private val matcher = RoutineMatcher(comparator, resampler, thresholds)

    fun signals(): List<DeviationSignal> = listOf(
        OffRouteSignal(thresholds),
        SequenceDivergenceSignal(thresholds, comparator),
        DelaySignal(thresholds),
        LoopSignal(thresholds),
        ErraticHeadingSignal(thresholds),
        MovingAwaySignal(thresholds),
        UnusualStopSignal(thresholds)
    )

    fun detectionEngine(signals: List<DeviationSignal> = signals()): LostDetectionEngine {
        val scorer = CompositeAnomalyScorer(signals, config.confusionWeights)
        return LostDetectionEngine(
            resampler = resampler.incremental(),
            modeFactory = DetectionModeFactory(matcher, scorer, config),
            classifier = AssistanceLevelClassifier(),
            voluntaryDetector = VoluntaryDetourDetector(config),
            explanationBuilder = DeviationExplanationBuilder(thresholds.reasonMinScore)
        )
    }

    fun routineLearner(idGenerator: IdGenerator): RoutineLearner = RoutineLearner(
        resampler = resampler,
        matcher = matcher,
        statistics = RunningStatistics(),
        confirmationPolicy = RoutineConfirmationPolicy(thresholds.routineConfirmationRepetitions),
        idGenerator = idGenerator
    )

    fun guidanceCalculator(): NearestRoutePointGuide = NearestRoutePointGuide()
}
