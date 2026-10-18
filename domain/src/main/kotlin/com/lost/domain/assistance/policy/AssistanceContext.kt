package com.lost.domain.assistance.policy

import com.lost.domain.detection.DetectionResult

/** Datos con los que cada eslabón de la cadena decide si actúa. */
data class AssistanceContext(
    val result: DetectionResult,
    val hasUserRequestedGuidance: Boolean,
    val hasResponseTimedOut: Boolean,
    val hasAuthorizedContact: Boolean
)
