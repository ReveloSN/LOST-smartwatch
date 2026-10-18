package com.lost.ai.feedback

import com.lost.ai.config.FeedbackSteps
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.port.ThresholdAdjuster

/**
 * Aprende de las respuestas: "Estoy bien" sube los umbrales (pregunta menos en casos así),
 * una ayuda confirmada los baja. Siempre dentro de límites para no silenciar a LOST.
 */
class PersonalThresholdAdjuster(private val steps: FeedbackSteps = FeedbackSteps.DEFAULT) : ThresholdAdjuster {
    override fun adjust(current: PersonalThresholds, feedback: UserFeedback): PersonalThresholds {
        val delta = when (feedback) {
            UserFeedback.IM_FINE -> steps.imFineStep
            UserFeedback.GOING_ELSEWHERE -> steps.goingElsewhereStep
            UserFeedback.HELP_CONFIRMED -> -steps.helpConfirmedStep
        }
        val medium = (current.medium + delta).coerceIn(steps.minMedium, steps.maxMedium)
        val high = (current.high + delta).coerceIn(medium + steps.minGap, steps.maxHigh)
        return PersonalThresholds(medium = medium, high = high)
    }
}
