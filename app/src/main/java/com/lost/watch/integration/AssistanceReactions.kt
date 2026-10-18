package com.lost.watch.integration

import com.lost.domain.assistance.AssistanceFeedbackLearner
import com.lost.domain.metrics.AssistanceMetricsRecorder
import com.lost.domain.session.AssistanceTransition
import javax.inject.Inject

/** Reacciones que pueden tardar (base de datos, red), agrupadas para no pasar muchos parámetros. */
class AssistanceReactions @Inject constructor(
    private val metrics: AssistanceMetricsRecorder,
    private val learner: AssistanceFeedbackLearner,
    private val sharing: LocationSharingEffect
) {
    suspend fun onTransition(transition: AssistanceTransition) {
        metrics.onTransition(transition)
        learner.onTransition(transition)
        sharing.onTransition(transition)
    }
}
