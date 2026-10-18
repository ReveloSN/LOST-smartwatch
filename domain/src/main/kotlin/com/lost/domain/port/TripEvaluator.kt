package com.lost.domain.port

import com.lost.domain.detection.DetectionRequest
import com.lost.domain.detection.DetectionResult

/** El motor de IA visto desde el dominio: evalúa un recorrido en curso. */
fun interface TripEvaluator {
    fun evaluate(request: DetectionRequest): DetectionResult
}
