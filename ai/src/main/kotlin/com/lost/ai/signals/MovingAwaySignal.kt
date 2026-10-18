package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.domain.detection.DeviationReason

/** Tendencia a alejarse del destino: cuánto ha crecido la distancia desde lo más cerca que estuvo. */
class MovingAwaySignal(
    private val thresholds: DetectionThresholds,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) : DeviationSignal {
    override val kind = SignalKind.MOVING_AWAY

    override fun measure(context: SignalContext): SignalReading {
        val target = context.target
        if (target == null || context.path.size < 2) return SignalReading.notApplicable(kind)
        val distances = context.path.map { distanceCalculator.distance(it, target).value }
        val increase = distances.last() - distances.min()
        val score = LinearScore.between(
            increase,
            thresholds.movingAwayMinMeters,
            thresholds.movingAwaySaturationMeters
        )
        return SignalReading(kind, score, DeviationReason.MovingAwayFromDestination.takeIf { score > 0.0 })
    }
}
