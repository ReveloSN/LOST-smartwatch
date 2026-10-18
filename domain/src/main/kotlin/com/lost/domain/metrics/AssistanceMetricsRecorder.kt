package com.lost.domain.metrics

import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.port.TimeSource
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.usecase.TrackMetricEventUseCase
import javax.inject.Inject

/**
 * Instrumenta la escalada (sección 9.4): alerta mostrada, tiempo hasta que el usuario decide,
 * tiempo hasta recuperar la ruta y llegadas al destino.
 */
class AssistanceMetricsRecorder @Inject constructor(
    private val trackMetric: TrackMetricEventUseCase,
    private val timeSource: TimeSource
) {
    private var alertShownAt: Long? = null
    private var guidanceStartedAt: Long? = null

    suspend fun onTransition(transition: AssistanceTransition) {
        val now = timeSource.nowMillis()
        recordAlert(transition, now)
        recordGuidance(transition, now)
        if (transition.event == AssistanceEvent.DestinationReached) trackMetric(MetricEventType.DESTINATION_REACHED)
    }

    private suspend fun recordAlert(transition: AssistanceTransition, now: Long) {
        if (isNewAlert(transition)) {
            alertShownAt = now
            trackMetric(MetricEventType.ALERT_SHOWN)
        }
        if (isUserResponse(transition)) {
            alertShownAt?.let { trackMetric(MetricEventType.USER_RESPONDED, now - it) }
            alertShownAt = null
        }
    }

    private suspend fun recordGuidance(transition: AssistanceTransition, now: Long) {
        if (transition.to == AssistanceState.Guiding &&
            transition.from != AssistanceState.Guiding
        ) {
            guidanceStartedAt = now
        }
        if (transition.event == AssistanceEvent.RouteRecovered && transition.from == AssistanceState.Guiding) {
            guidanceStartedAt?.let { trackMetric(MetricEventType.ROUTE_RECOVERED, now - it) }
        }
    }

    private fun isNewAlert(transition: AssistanceTransition): Boolean {
        val to = transition.to
        val from = transition.from
        return (to is AssistanceState.SoftAsk || to is AssistanceState.AskForHelp) &&
            from !is AssistanceState.SoftAsk && from !is AssistanceState.AskForHelp
    }

    private fun isUserResponse(transition: AssistanceTransition): Boolean = transition.event in USER_RESPONSES &&
        (transition.from is AssistanceState.SoftAsk || transition.from is AssistanceState.AskForHelp)

    private companion object {
        val USER_RESPONSES = setOf(
            AssistanceEvent.UserRequestedGuidance,
            AssistanceEvent.UserIsFine,
            AssistanceEvent.UserIsGoingElsewhere
        )
    }
}
