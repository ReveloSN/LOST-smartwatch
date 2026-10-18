package com.lost.domain.usecase

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionRequest
import com.lost.domain.detection.DetectionResult
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeRoutineRepository
import com.lost.domain.fakes.FakeSettingsRepository
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.UserSettings
import com.lost.domain.port.TripEvaluator
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class EvaluateCurrentTripUseCaseTest {
    private var lastRequest: DetectionRequest? = null
    private val evaluator = TripEvaluator { request ->
        lastRequest = request
        DomainFixtures.result(AssistanceLevel.MEDIUM)
    }
    private val settings = FakeSettingsRepository(UserSettings.DEFAULT.copy(sensitivity = Sensitivity.HIGH))
    private val useCase = EvaluateCurrentTripUseCase(
        evaluator,
        FakeRoutineRepository(listOf(DomainFixtures.routine())),
        settings
    )

    @Test
    fun `un recorrido con muy pocos puntos no se evalua`() = runTest {
        assertEquals(DetectionResult.CALM, useCase(DomainFixtures.trip(points = 2)))
        assertNull(lastRequest)
    }

    @Test
    fun `envia al motor las rutinas y las preferencias del usuario`() = runTest {
        val result = useCase(DomainFixtures.trip())
        assertEquals(AssistanceLevel.MEDIUM, result.level)
        assertEquals(1, lastRequest!!.routines.size)
        assertEquals(Sensitivity.HIGH, lastRequest!!.sensitivity)
    }
}
