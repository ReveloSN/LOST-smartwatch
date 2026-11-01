package com.lost.ai.geometry

import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * El reloj dice qué tan seguro está de cada punto (`Location.accuracy`). Un punto que el mismo GPS marca
 * como malo no debe crear giros ni zonas repetidas, y entre dos puntos buenos pesa más el más preciso.
 */
class GpsAccuracyTest {
    private val resampler = TrajectoryResampler(SPACING)
    private val projection = LocalProjection(TripFixtures.ORIGIN)
    private val distance = HaversineDistanceCalculator()

    /** 1 km hacia el este a 1,4 m/s, un punto cada 5 s, todos con [accuracy] metros de precisión. */
    private fun line(accuracy: Double?): List<GeoPoint> = (0..STEPS).map { i ->
        TripFixtures.at(i * STEP, 0.0).copy(timestampMillis = i * SAMPLE_MILLIS, accuracyMeters = accuracy)
    }

    /** Dos saltos de 60 m seguidos: el filtro de velocidad solo descarta el primero. */
    private fun withDoubleSpike(points: List<GeoPoint>, accuracy: Double): List<GeoPoint> =
        points.mapIndexed { i, point ->
            if (i == STEPS / 2 || i == STEPS / 2 + 1) {
                TripFixtures.at(
                    i * STEP,
                    SPIKE_METERS
                ).copy(timestampMillis = point.timestampMillis, accuracyMeters = accuracy)
            } else {
                point
            }
        }

    /** Cuánto se aparta el rastro remuestreado de la calle (la línea y = 0), en metros. */
    private fun maxOffsetFromStreet(path: List<GeoPoint>): Double =
        path.maxOf { kotlin.math.abs(projection.toMeters(it).y) }

    @Test
    fun `dos saltos seguidos que el gps marca como imprecisos no apartan el rastro de la calle`() {
        val offset = maxOffsetFromStreet(resampler.resample(withDoubleSpike(line(GOOD), accuracy = BAD)))
        assertTrue(offset < 1.0, "el rastro se apartó $offset m")
    }

    @Test
    fun `sin la precision los mismos dos saltos si apartan el rastro`() {
        val offset = maxOffsetFromStreet(resampler.resample(withDoubleSpike(line(null), accuracy = GOOD)))
        assertTrue(
            offset > MIN_HARMFUL_OFFSET,
            "la prueba anterior no probaría nada: el rastro solo se apartó $offset m"
        )
    }

    @Test
    fun `sin precision conocida el remuestreo es el de siempre`() {
        assertEquals(
            resampler.resample(line(null)),
            resampler.resample(line(GOOD)).map {
                it.copy(accuracyMeters = null)
            }
        )
    }

    @Test
    fun `un recorrido que empieza con un punto impreciso empieza en el primero preciso`() {
        val points = line(GOOD)
        val badStart = listOf(TripFixtures.at(0.0, SPIKE_METERS).copy(accuracyMeters = BAD)) +
            points.map { it.copy(timestampMillis = it.timestampMillis + SAMPLE_MILLIS) }
        val first = resampler.resample(badStart).first()
        assertTrue(distance.distance(first, points.first()).value < 1.0, "empezó en el punto impreciso")
    }

    @Test
    fun `el promedio pesa mas el punto mas preciso`() {
        val average = GpsWindowAverage(smoothingSeconds = 15.0, accuracyFloorMeters = 5.0)
        average.add(TripFixtures.at(0.0, 0.0).copy(timestampMillis = 0L, accuracyMeters = 5.0))
        val smoothed = average.add(TripFixtures.at(0.0, 30.0).copy(timestampMillis = 5_000L, accuracyMeters = 30.0))
        // Pesos 1/25 y 1/900: el promedio queda a menos de 1 m del punto preciso (sin pesos, a 15 m).
        assertTrue(distance.distance(smoothed, TripFixtures.at(0.0, 0.0)).value < 1.0)
    }

    @Test
    fun `el remuestreo incremental ignora los mismos puntos que el completo`() {
        val points = withDoubleSpike(line(GOOD), accuracy = BAD)
        val incremental = resampler.incremental()
        var last = emptyList<GeoPoint>()
        for (end in 2..points.size) last = incremental.resample(points.subList(0, end))
        assertEquals(resampler.resample(points), last)
    }

    private companion object {
        const val SPACING = 20.0
        const val STEPS = 143
        const val STEP = 7.0
        const val SAMPLE_MILLIS = 5_000L
        const val SPIKE_METERS = 60.0
        const val MIN_HARMFUL_OFFSET = 10.0
        const val GOOD = 8.0
        const val BAD = 70.0
    }
}
