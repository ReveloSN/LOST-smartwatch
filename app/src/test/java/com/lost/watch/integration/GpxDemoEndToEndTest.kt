package com.lost.watch.integration

import com.lost.data.demo.DemoScenario
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.model.Destination
import com.lost.domain.model.PlaceKind
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Demo de la fase 6 sin emulador: reproduce los GPX de `sample-routes/` por la tubería real
 * (motor de IA → monitor → máquina de estados) y comprueba la historia de cada escenario.
 */
class GpxDemoEndToEndTest {
    private val harness = GpxStoryHarness()

    @Test
    fun `ruta de siempre LOST solo observa`() = runTest {
        val story = harness.story(listOf(harness.casaU))
        with(harness) { story.play(gpx(DemoScenario.NORMAL_ROUTINE)) }
        assertTrue(story.states.all { it == AssistanceState.Calm }, "estados ${story.states}")
        assertTrue(story.session.snapshot.value.hasArrived)
    }

    @Test
    fun `desvio voluntario solo pregunta suave`() = runTest {
        val story = harness.story(listOf(harness.casaU))
        with(harness) { story.play(gpx(DemoScenario.VOLUNTARY_DETOUR)) }
        assertTrue(story.states.any { it is AssistanceState.SoftAsk }, "estados ${story.states}")
        assertFalse(story.states.any { it is AssistanceState.AskForHelp }, "estados ${story.states}")
    }

    @Test
    fun `calle cerrada detecta el desvio guia de regreso y recupera la ruta`() = runTest {
        val story = harness.story(listOf(harness.casaU))
        with(harness) { story.play(gpx(DemoScenario.DISORIENTATION)) }
        val askIndex = story.states.indexOfFirst {
            it is AssistanceState.AskForHelp ||
                it is AssistanceState.SharingCountdown
        }
        val guideIndex = story.states.indexOf(AssistanceState.Guiding)
        assertTrue(askIndex >= 0, "nunca preguntó: ${story.states}")
        assertTrue(guideIndex > askIndex, "no guió después de preguntar: ${story.states}")
        assertEquals(AssistanceState.Calm, story.states.last(), "no recuperó la ruta: ${story.states}")
    }

    @Test
    fun `hospital sin rutina pregunta al dar vueltas`() = runTest {
        val points = harness.gpx(DemoScenario.NEW_PLACE)
        val room = Destination("room", "Consultorio 304", points.last(), PlaceKind.HOSPITAL, null)
        val story = harness.story(emptyList())
        with(harness) { story.play(points, room) }
        assertTrue(
            story.states.any {
                it is AssistanceState.AskForHelp || it is AssistanceState.Guiding
            },
            "estados ${story.states}"
        )
        assertTrue(story.session.snapshot.value.hasArrived)
    }
}
