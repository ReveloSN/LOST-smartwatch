package com.lost.ai

import com.lost.ai.feedback.PersonalThresholdAdjuster
import com.lost.ai.fixtures.EngineRequests.request
import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.assistance.AssistanceStateMachine
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.detection.DetectionModeKind
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.PersonalThresholds
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Las cinco pruebas de escenario obligatorias de la sección 8.5 del plan. */
class DetectionScenariosTest {
    private val engine = LostEngineAssembly().detectionEngine()
    private val stateMachine = AssistanceStateMachine()

    @Test
    fun `rutina normal da nivel bajo y no molesta`() {
        val result = engine.evaluate(request(TripFixtures.normalRoutineTrip()))

        assertEquals(DetectionModeKind.ROUTINE, result.mode)
        assertEquals("casa-u", result.routineId)
        assertEquals(AssistanceLevel.LOW, result.level)
        assertFalse(result.isVoluntaryDetour)
        assertTrue(result.routeProgress > 0.5, "avance ${result.routeProgress}")
    }

    @Test
    fun `desvio voluntario termina en pregunta suave`() {
        val result = engine.evaluate(request(TripFixtures.voluntaryDetourTrip()))

        assertEquals(AssistanceLevel.LOW, result.level, "probabilidad ${result.probability}")
        assertTrue(result.isVoluntaryDetour)
        val state = stateMachine.next(AssistanceState.Calm, AssistanceEvent.DetectionUpdated(result, true))
        assertTrue(state is AssistanceState.SoftAsk, "estado $state")
    }

    @Test
    fun `desorientacion con bucles sube a nivel medio o alto y explica por que`() {
        val result = engine.evaluate(request(TripFixtures.disorientedTrip()))

        assertTrue(result.level >= AssistanceLevel.MEDIUM, "nivel ${result.level} con ${result.probability}")
        assertFalse(result.isVoluntaryDetour)
        assertTrue(result.reasons.any { it is DeviationReason.LeftUsualRoute })
        val repeated = result.reasons.filterIsInstance<DeviationReason.RepeatedArea>().single()
        assertTrue(repeated.times >= 3, "pasadas ${repeated.times}")
    }

    @Test
    fun `lugar nuevo dando vueltas sube a nivel medio`() {
        val trip = TripFixtures.newPlaceLoopsTrip()
        val result = engine.evaluate(request(trip, routines = emptyList(), destination = TripFixtures.HOSPITAL_ROOM))

        assertEquals(DetectionModeKind.NEW_PLACE, result.mode)
        assertEquals(AssistanceLevel.MEDIUM, result.level, "probabilidad ${result.probability}")
        assertTrue(result.reasons.any { it is DeviationReason.RepeatedArea })
    }

    @Test
    fun `estoy bien repetido sube el umbral y deja de preguntar en casos asi`() {
        val adjuster = PersonalThresholdAdjuster()
        var thresholds = PersonalThresholds.DEFAULT
        val trip = TripFixtures.newPlaceLoopsTrip()
        val before = engine.evaluate(request(trip, emptyList(), TripFixtures.HOSPITAL_ROOM, thresholds))

        repeat(3) { thresholds = adjuster.adjust(thresholds, UserFeedback.IM_FINE) }
        val after = engine.evaluate(request(trip, emptyList(), TripFixtures.HOSPITAL_ROOM, thresholds))

        assertTrue(thresholds.medium > PersonalThresholds.DEFAULT.medium)
        assertEquals(AssistanceLevel.MEDIUM, before.level)
        assertEquals(AssistanceLevel.LOW, after.level)
    }
}
