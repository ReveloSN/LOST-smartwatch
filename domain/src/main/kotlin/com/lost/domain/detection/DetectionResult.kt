package com.lost.domain.detection

import com.lost.domain.model.units.Probability

/**
 * Resultado de evaluar el recorrido actual.
 * @property isVoluntaryDetour desvío alto pero camina seguro: solo se pregunta suave.
 * @property routeProgress avance (0..1) sobre la ruta de siempre; 0 si no hay rutina.
 */
data class DetectionResult(
    val probability: Probability,
    val level: AssistanceLevel,
    val reasons: List<DeviationReason>,
    val isVoluntaryDetour: Boolean,
    val mode: DetectionModeKind,
    val routineId: String?,
    val routeProgress: Double
) {
    val isDeviating: Boolean get() = reasons.isNotEmpty()

    companion object {
        val CALM = DetectionResult(
            probability = Probability.ZERO,
            level = AssistanceLevel.LOW,
            reasons = emptyList(),
            isVoluntaryDetour = false,
            mode = DetectionModeKind.NEW_PLACE,
            routineId = null,
            routeProgress = 0.0
        )
    }
}
