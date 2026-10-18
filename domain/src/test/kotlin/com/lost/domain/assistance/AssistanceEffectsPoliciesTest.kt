package com.lost.domain.assistance

import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeSettingsRepository
import com.lost.domain.fakes.RecordingMetricsTracker
import com.lost.domain.metrics.AssistanceMetricsRecorder
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.port.ThresholdAdjuster
import com.lost.domain.session.AssistanceTransition
import com.lost.domain.usecase.ApplyUserFeedbackUseCase
import com.lost.domain.usecase.TrackMetricEventUseCase
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class AssistanceEffectsPoliciesTest {
    private val asking = AssistanceState.AskForHelp(DomainFixtures.reasons)
    private val detected = AssistanceEvent.DetectionUpdated(
        DomainFixtures.result(com.lost.domain.detection.AssistanceLevel.MEDIUM),
        true
    )

    @Test
    fun `cada estado tiene su vibracion y no se repite si no cambia`() {
        val policy = AssistanceHapticPolicy()
        assertEquals(
            HapticPattern.DOUBLE,
            policy.patternFor(AssistanceTransition(AssistanceState.Calm, asking, detected))
        )
        assertNull(policy.patternFor(AssistanceTransition(asking, asking, detected)))
        assertEquals(
            HapticPattern.INSISTENT,
            policy.patternFor(AssistanceTransition(asking, AssistanceState.SharingCountdown(5), detected))
        )
        assertEquals(
            HapticPattern.GENTLE,
            policy.patternFor(
                AssistanceTransition(AssistanceState.Guiding, AssistanceState.Calm, AssistanceEvent.RouteRecovered)
            )
        )
    }

    @Test
    fun `las metricas miden alerta respuesta y recuperacion`() = runTest {
        val tracker = RecordingMetricsTracker()
        var now = 0L
        val recorder = AssistanceMetricsRecorder(TrackMetricEventUseCase(tracker) { now }) { now }

        recorder.onTransition(AssistanceTransition(AssistanceState.Calm, asking, detected))
        now = 2_000
        recorder.onTransition(
            AssistanceTransition(asking, AssistanceState.Guiding, AssistanceEvent.UserRequestedGuidance)
        )
        now = 27_000
        recorder.onTransition(
            AssistanceTransition(AssistanceState.Guiding, AssistanceState.Calm, AssistanceEvent.RouteRecovered)
        )

        assertEquals(
            listOf(MetricEventType.ALERT_SHOWN, MetricEventType.USER_RESPONDED, MetricEventType.ROUTE_RECOVERED),
            tracker.types
        )
        assertEquals(2_000L, tracker.events[1].elapsedMillis)
        assertEquals(25_000L, tracker.events[2].elapsedMillis)
    }

    @Test
    fun `aceptar la guia cuenta como ayuda confirmada`() = runTest {
        val settings = FakeSettingsRepository()
        val lowered = PersonalThresholds(0.4, 0.7)
        val learner = AssistanceFeedbackLearner(
            ApplyUserFeedbackUseCase(
                settings,
                ThresholdAdjuster { _, _ ->
                    lowered
                },
                TrackMetricEventUseCase(RecordingMetricsTracker()) { 0L }
            )
        )
        learner.onTransition(
            AssistanceTransition(asking, AssistanceState.Guiding, AssistanceEvent.UserRequestedGuidance)
        )
        assertEquals(lowered, settings.state.value.thresholds)
    }
}
