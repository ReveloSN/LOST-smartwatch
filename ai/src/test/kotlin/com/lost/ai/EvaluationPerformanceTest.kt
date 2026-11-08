package com.lost.ai

import com.lost.ai.fixtures.EngineRequests
import com.lost.ai.fixtures.SyntheticWalk
import com.lost.ai.fixtures.TripFixtures
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Trip
import kotlin.system.measureNanoTime
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Mide el costo del motor con un GPS real (un punto por segundo, no cada 5 s como la demo).
 * Los números se imprimen para anotarlos en docs/AVANCES.md (antes/después de optimizar).
 */
class EvaluationPerformanceTest {
    private val engine = LostEngineAssembly().detectionEngine()

    /** 35 min a 1 Hz con 3 vueltas a la manzana: el caso más pesado de la demo (~2 100 puntos). */
    private val longTrip: Trip = Trip(
        "long",
        everySecond(
            SyntheticWalk().to(300.0, 0.0).to(300.0, -150.0).square(side = 60.0, times = 3)
                .to(300.0, 0.0).to(600.0, 0.0).to(600.0, 500.0).stay(seconds = 600).points()
        )
    )

    @Test
    fun `una evaluacion tipica tarda menos de 50 ms`() {
        repeat(WARMUP) { engine.evaluate(EngineRequests.request(longTrip)) }
        val averageMillis = measureNanoTime {
            repeat(RUNS) { engine.evaluate(EngineRequests.request(longTrip)) }
        } / RUNS / NANOS_PER_MILLI
        println("Evaluación de ${longTrip.points.size} puntos: ${"%.2f".format(averageMillis)} ms en promedio")
        assertTrue(averageMillis < MAX_MILLIS, "tardó $averageMillis ms")
    }

    @Test
    fun `seguir un recorrido completo punto a punto`() {
        val prefixes = (STEP..longTrip.points.size step STEP).map { Trip("t", longTrip.points.take(it)) }
        val totalMillis = measureNanoTime {
            prefixes.forEach { engine.evaluate(EngineRequests.request(it)) }
        } / NANOS_PER_MILLI
        println("Recorrido completo, una evaluación cada $STEP s: ${"%.0f".format(totalMillis)} ms en total")
        assertTrue(totalMillis < MAX_TOTAL_MILLIS, "tardó $totalMillis ms")
    }

    /**
     * Rutina larga como la de la demo (Catambuco → Universidad de Nariño, ~11 km con 12 giros): LOST la sigue
     * punto a punto (un punto cada 5 s). Antes de reutilizar el DTW esto crecía con el cuadrado del largo.
     */
    @Test
    fun `seguir una rutina larga de 11 km punto a punto`() {
        val walk = SyntheticWalk()
        repeat(LONG_ROUTE_LEGS) { leg -> walk.to((leg + 1) * LEG_METERS, if (leg % 2 == 0) LEG_METERS else 0.0) }
        val points = walk.points()
        val routine = TripFixtures.casaU().copy(path = TrajectoryResampler(RESAMPLE_METERS).resample(points))
        val prefixes = (STEP..points.size step STEP).map { Trip("t", points.take(it)) }
        val totalMillis = measureNanoTime {
            prefixes.forEach { engine.evaluate(EngineRequests.request(it, listOf(routine))) }
        } / NANOS_PER_MILLI
        println(
            "Rutina de ${routine.path.size} puntos, ${prefixes.size} evaluaciones: ${"%.0f".format(totalMillis)} ms"
        )
        assertTrue(totalMillis < MAX_LONG_ROUTE_MILLIS, "tardó $totalMillis ms")
    }

    /** Convierte un recorrido con un punto cada 5 s en uno con un punto por segundo. */
    private fun everySecond(points: List<GeoPoint>): List<GeoPoint> = points.zipWithNext { a, b ->
        (0 until SUBSTEPS).map { i ->
            val f = i.toDouble() / SUBSTEPS
            GeoPoint(
                a.latitude + (b.latitude - a.latitude) * f,
                a.longitude + (b.longitude - a.longitude) * f,
                a.timestampMillis + ((b.timestampMillis - a.timestampMillis) * f).toLong()
            )
        }
    }.flatten() + points.last()

    private companion object {
        const val WARMUP = 5
        const val RUNS = 20
        const val NANOS_PER_MILLI = 1_000_000.0
        const val MAX_MILLIS = 50.0
        const val MAX_TOTAL_MILLIS = 20_000.0
        const val SUBSTEPS = 5
        const val STEP = 5
        const val LONG_ROUTE_LEGS = 12
        const val LEG_METERS = 650.0
        const val RESAMPLE_METERS = 20.0
        const val MAX_LONG_ROUTE_MILLIS = 60_000.0
    }
}
