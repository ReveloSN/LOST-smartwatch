package com.lost.ai.mode

import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.model.GeoPoint

/**
 * Estrategia de detección. [RoutineDetectionMode] y [NewPlaceDetectionMode] son
 * intercambiables: el motor no sabe cuál está usando (sustitución de Liskov).
 */
interface DetectionMode {
    val kind: DetectionModeKind
    val routineId: String?

    fun evaluate(path: List<GeoPoint>, request: DetectionRequest): ModeEvaluation
}
