package com.lost.domain.usecase

import com.lost.domain.assistance.UserFeedback
import com.lost.domain.fakes.FakeSettingsRepository
import com.lost.domain.fakes.RecordingMetricsTracker
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.port.ThresholdAdjuster
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ApplyUserFeedbackUseCaseTest {
    private val settings = FakeSettingsRepository()
    private val metrics = RecordingMetricsTracker()
    private val raised = PersonalThresholds(0.5, 0.8)
    private val useCase = ApplyUserFeedbackUseCase(
        settings,
        ThresholdAdjuster { _, _ -> raised },
        TrackMetricEventUseCase(metrics) { 1L }
    )

    @Test
    fun `estoy bien guarda los nuevos umbrales y cuenta un falso positivo`() = runTest {
        useCase(UserFeedback.IM_FINE)
        assertEquals(raised, settings.state.value.thresholds)
        assertEquals(listOf(MetricEventType.ALERT_DISMISSED_IM_FINE), metrics.types)
    }

    @Test
    fun `ayuda confirmada cuenta una asistencia iniciada`() = runTest {
        useCase(UserFeedback.HELP_CONFIRMED)
        assertEquals(listOf(MetricEventType.ASSISTANCE_STARTED), metrics.types)
    }
}
