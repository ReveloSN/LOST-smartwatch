package com.lost.watch.integration

import com.lost.data.demo.DemoScenario
import com.lost.domain.assistance.AssistanceEvent
import com.lost.domain.assistance.AssistanceState
import com.lost.domain.model.Destination
import com.lost.domain.model.PlaceKind
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

/**
 * Los 4 escenarios de la demo con el ruido de un GPS real: el motor debe contar la misma historia
 * (sin falsas alarmas en la ruta de siempre y sin perder la desorientación), con o sin la precisión
 * que informa el reloj en cada punto.
 */
class GpsNoiseRobustnessTest {
    private val harness = GpxStoryHarness()

    @TestFactory
    fun `la historia de cada escenario resiste el ruido realista`() =
        realisticWithAndWithoutAccuracy().flatMap { noise ->
            listOf(
                DynamicTest.dynamicTest("ruta de siempre · ${noise.name}") { normal(noise) },
                DynamicTest.dynamicTest("desvío voluntario · ${noise.name}") { voluntary(noise) },
                DynamicTest.dynamicTest("calle cerrada · ${noise.name}") { disorientation(noise) },
                DynamicTest.dynamicTest("hospital · ${noise.name}") { newPlace(noise) }
            )
        }

    /**
     * Una sola semilla puede tener suerte: las falsas alarmas (ruta de siempre y desvío voluntario) se prueban
     * con 10 ruidos distintos por variante. En el desvío voluntario nadie responde "¿Vas a otro lado hoy?",
     * el caso más exigente. La desorientación y el hospital nunca fallaron con varias semillas (ver
     * docs/ia/robustez-ruido-gps.md) y se dejan con una para no alargar las pruebas.
     */
    @TestFactory
    fun `sin falsas alarmas con 10 semillas por variante`() = realisticWithAndWithoutAccuracy().flatMap { noise ->
        SEEDS.flatMap { seed ->
            val seeded = noise.copy(seed = seed)
            listOf(
                DynamicTest.dynamicTest("ruta de siempre · ${noise.name} · semilla $seed") { normal(seeded) },
                DynamicTest.dynamicTest("desvío voluntario · ${noise.name} · semilla $seed") { voluntary(seeded) }
            )
        }
    }

    /**
     * La historia completa del desvío voluntario: "¿Vas a otro lado hoy?" → "Voy a otro lado" → LOST ya no
     * compara con la ruta de siempre y no vuelve a preguntar en todo el recorrido.
     */
    @TestFactory
    fun `tras voy a otro lado no vuelve a preguntar`() = SEEDS.map { seed ->
        DynamicTest.dynamicTest("ciudad con rebotes · semilla $seed") {
            val story = harness.story(listOf(harness.casaU))
            val noise = GpsNoise.REALISTIC[2].copy(seed = seed)
            val points = noise.apply(harness.gpx(DemoScenario.VOLUNTARY_DETOUR))
            runTest { with(harness) { story.play(points, softAskAnswer = AssistanceEvent.UserIsGoingElsewhere) } }
            val afterAnswer = story.states.dropWhile { it !is AssistanceState.SoftAsk }.drop(1)
            assertTrue(story.states.any { it is AssistanceState.SoftAsk }, "no preguntó: ${story.states}")
            assertTrue(afterAnswer.all { it is AssistanceState.Calm }, "volvió a preguntar: ${story.states}")
        }
    }

    @Test
    fun `en un cañón urbano la precisión del gps evita la falsa alarma en la ruta de siempre`() {
        normal(GpsNoise.URBAN_CANYON.reporting(AccuracyReport.HONEST))
    }

    @Test
    fun `en un cañón urbano un desvío voluntario no pide ayuda`() {
        voluntary(GpsNoise.URBAN_CANYON.reporting(AccuracyReport.HONEST))
    }

    @Test
    fun `en un cañón urbano se siguen notando la desorientación y las vueltas`() {
        val canyon = GpsNoise.URBAN_CANYON.reporting(AccuracyReport.HONEST)
        disorientation(canyon)
        newPlace(canyon)
    }

    private fun realisticWithAndWithoutAccuracy() =
        GpsNoise.REALISTIC + GpsNoise.REALISTIC.map { it.reporting(AccuracyReport.HONEST) }

    private fun play(scenario: DemoScenario, noise: GpsNoise): GpxStoryHarness.Story {
        val points = noise.apply(harness.gpx(scenario))
        val isNewPlace = scenario == DemoScenario.NEW_PLACE
        val story = harness.story(if (isNewPlace) emptyList() else listOf(harness.casaU))
        // El destino del hospital se marca con el último punto del GPX sin ruido (lo eligió el usuario).
        val room = harness.gpx(scenario).last().takeIf { isNewPlace }
            ?.let { Destination("room", "Consultorio 304", it, PlaceKind.HOSPITAL, null) }
        runTest { with(harness) { story.play(points, room) } }
        return story
    }

    private fun normal(noise: GpsNoise) {
        val states = play(DemoScenario.NORMAL_ROUTINE, noise).states
        assertTrue(states.all { it == AssistanceState.Calm }, "falsa alarma: $states")
    }

    private fun voluntary(noise: GpsNoise) {
        val states = play(DemoScenario.VOLUNTARY_DETOUR, noise).states
        assertTrue(states.none { it is AssistanceState.AskForHelp }, "pidió ayuda en un desvío voluntario: $states")
    }

    private fun disorientation(noise: GpsNoise) {
        val states = play(DemoScenario.DISORIENTATION, noise).states
        assertTrue(
            states.any { it is AssistanceState.AskForHelp || it is AssistanceState.SharingCountdown },
            "no detectó la desorientación: $states"
        )
    }

    private fun newPlace(noise: GpsNoise) {
        val states = play(DemoScenario.NEW_PLACE, noise).states
        assertTrue(
            states.any { it is AssistanceState.AskForHelp || it is AssistanceState.Guiding },
            "no notó las vueltas: $states"
        )
    }

    private companion object {
        val SEEDS = 1..10
    }
}
