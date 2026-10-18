package com.lost.ai.scoring

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.units.Probability

/** Convierte la probabilidad en nivel Bajo / Medio / Alto con los umbrales personales del usuario. */
class AssistanceLevelClassifier {
    fun classify(probability: Probability, thresholds: PersonalThresholds): AssistanceLevel = when {
        probability.value >= thresholds.high -> AssistanceLevel.HIGH
        probability.value >= thresholds.medium -> AssistanceLevel.MEDIUM
        else -> AssistanceLevel.LOW
    }
}
