package com.lost.ai.signals

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.PolylineDistanceCalculator
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import kotlin.math.roundToInt

/** Qué tan lejos está el usuario de su ruta de siempre en este momento. */
class OffRouteSignal(
    private val thresholds: DetectionThresholds,
    private val polyline: PolylineDistanceCalculator = PolylineDistanceCalculator()
) : DeviationSignal {
    override val kind = SignalKind.OFF_ROUTE

    override fun measure(context: SignalContext): SignalReading {
        val current = context.path.lastOrNull()
        if (!context.hasRoutine || current == null) return SignalReading.notApplicable(kind)
        val distance = polyline.distanceToPath(current, context.routinePath).value
        val score = LinearScore.between(
            distance,
            thresholds.offRouteToleranceMeters,
            thresholds.offRouteSaturationMeters
        )
        val reason = DeviationReason.LeftUsualRoute(Meters(distance.roundToInt().toDouble()))
        return SignalReading(kind, score, reason.takeIf { score > 0.0 })
    }
}
