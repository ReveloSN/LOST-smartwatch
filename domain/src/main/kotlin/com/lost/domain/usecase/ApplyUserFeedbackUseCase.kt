package com.lost.domain.usecase

import com.lost.domain.assistance.UserFeedback
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.port.SettingsWriter
import com.lost.domain.port.ThresholdAdjuster
import javax.inject.Inject

/**
 * Usa la respuesta del usuario para aprender: mueve los umbrales personales
 * y registra el evento de métricas correspondiente (falsos positivos o ayuda iniciada).
 */
class ApplyUserFeedbackUseCase @Inject constructor(
    private val settingsWriter: SettingsWriter,
    private val adjuster: ThresholdAdjuster,
    private val trackMetric: TrackMetricEventUseCase
) {
    suspend operator fun invoke(feedback: UserFeedback) {
        settingsWriter.update { it.copy(thresholds = adjuster.adjust(it.thresholds, feedback)) }
        val metric = when (feedback) {
            UserFeedback.IM_FINE -> MetricEventType.ALERT_DISMISSED_IM_FINE
            UserFeedback.GOING_ELSEWHERE -> MetricEventType.ALERT_DISMISSED_GOING_ELSEWHERE
            UserFeedback.HELP_CONFIRMED -> MetricEventType.ASSISTANCE_STARTED
        }
        trackMetric(metric)
    }
}
