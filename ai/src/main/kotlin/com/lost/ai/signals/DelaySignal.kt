package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.domain.detection.DeviationReason
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

/**
 * Tiempo real frente al esperado en ese punto de la rutina. La tolerancia depende de la
 * sensibilidad: con una ruta de 35 min, en Media pregunta si se pasa de 50 min.
 */
class DelaySignal(private val thresholds: DetectionThresholds) : DeviationSignal {
    override val kind = SignalKind.DELAY

    override fun measure(context: SignalContext): SignalReading {
        val routine = context.routine
        if (!context.hasRoutine || routine == null || routine.duration.count == 0) {
            return SignalReading.notApplicable(kind)
        }
        val mean = routine.duration.meanMinutes
        val elapsedMinutes = context.trip.durationMillis.toDouble() / TimeUnit.MINUTES.toMillis(1)
        val lateness = elapsedMinutes - mean * context.progress
        val tolerance = mean * (context.sensitivity.toleranceFactor - 1.0) * thresholds.delayToleranceShare
        val score = LinearScore.between(lateness, 0.0, tolerance)
        return SignalReading(kind, score, DeviationReason.TakingLonger(lateness.roundToInt()).takeIf { score > 0.0 })
    }
}
