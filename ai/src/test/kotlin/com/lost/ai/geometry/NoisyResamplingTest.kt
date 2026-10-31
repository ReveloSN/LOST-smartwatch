package com.lost.ai.geometry

import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.model.GeoPoint
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * El GPS de un reloj tiembla unos metros en cada punto. El remuestreo no debe convertir ese temblor
 * en metros recorridos de más ni en giros bruscos: si no, el motor ve desvíos y vueltas que no existen.
 */
class NoisyResamplingTest {
    private val resampler = TrajectoryResampler(SPACING)
    private val bearings = BearingCalculator()

    /** 1 km hacia el este a 1,4 m/s, un punto cada 5 s, con error gaussiano de [sigma] metros. */
    private fun noisyLine(sigma: Double, spikeAt: Int? = null): List<GeoPoint> {
        val random = Random(SEED)
        return (0..STEPS).map { i ->
            val angle = random.nextDouble() * 2 * Math.PI
            val radius = sigma * kotlin.math.sqrt(-2 * kotlin.math.ln(1 - random.nextDouble()))
            val spike = if (i == spikeAt) SPIKE_METERS else 0.0
            TripFixtures.at(i * STEP + radius * cos(angle), radius * sin(angle) + spike)
                .copy(timestampMillis = i * SAMPLE_MILLIS)
        }
    }

    private fun sharpTurns(path: List<GeoPoint>): Int = bearings.bearings(path)
        .zipWithNext { a, b -> a.shortestDifferenceTo(b).value }
        .count { it >= SHARP_TURN_DEGREES }

    @Test
    fun `el temblor del gps no alarga el recorrido`() {
        val resampled = resampler.resample(noisyLine(sigma = 5.0))
        val expected = STEPS * STEP / SPACING
        assertTrue(resampled.size <= expected * 1.1, "${resampled.size} puntos para ${expected.toInt()} esperados")
    }

    @Test
    fun `el temblor del gps no se convierte en giros bruscos`() {
        val turns = sharpTurns(resampler.resample(noisyLine(sigma = 5.0)))
        assertTrue(turns <= 1, "$turns giros de más de $SHARP_TURN_DEGREES° en una línea recta")
    }

    @Test
    fun `el temblor de un gps en ciudad tampoco se convierte en giros bruscos`() {
        val turns = sharpTurns(resampler.resample(noisyLine(sigma = 10.0)))
        assertTrue(turns <= 2, "$turns giros de más de $SHARP_TURN_DEGREES° en una línea recta")
    }

    @Test
    fun `un salto aislado de la senal se descarta`() {
        val turns = sharpTurns(resampler.resample(noisyLine(sigma = 0.0, spikeAt = STEPS / 2)))
        assertTrue(turns == 0, "$turns giros por un solo salto de $SPIKE_METERS m")
    }

    private companion object {
        const val SPACING = 20.0
        const val STEPS = 143
        const val STEP = 7.0
        const val SAMPLE_MILLIS = 5_000L
        const val SPIKE_METERS = 40.0
        const val SHARP_TURN_DEGREES = 60.0
        const val SEED = 27
    }
}
