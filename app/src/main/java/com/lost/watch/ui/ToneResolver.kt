package com.lost.watch.ui

import com.lost.domain.detection.AssistanceLevel
import javax.inject.Inject

/** Elige el tono según el nivel: bajo (o desvío voluntario) → relajado, medio → atento, alto → firme. */
class ToneResolver @Inject constructor() {
    fun resolve(level: AssistanceLevel): Tone = when (level) {
        AssistanceLevel.HIGH -> Tone.FIRM
        AssistanceLevel.MEDIUM -> Tone.ATTENTIVE
        AssistanceLevel.LOW -> Tone.RELAXED
    }
}
