package com.lost.domain.port

import com.lost.domain.assistance.UserFeedback
import com.lost.domain.model.PersonalThresholds

/** Ajusta los umbrales personales según la respuesta del usuario. */
fun interface ThresholdAdjuster {
    fun adjust(current: PersonalThresholds, feedback: UserFeedback): PersonalThresholds
}
