package com.lost.ai.geometry

import com.lost.ai.fixtures.TripFixtures
import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GeometryTest {
    private val haversine = HaversineDistanceCalculator()

    @Test
    fun `un grado de latitud mide unos 111 km`() {
        assertEquals(111_195.0, haversine.distance(GeoPoint(0.0, 0.0), GeoPoint(1.0, 0.0)).value, 5.0)
    }

    @Test
    fun `la proyeccion local conserva las distancias cortas`() {
        assertEquals(500.0, haversine.distance(TripFixtures.at(0.0, 0.0), TripFixtures.at(300.0, 400.0)).value, 1.0)
    }

    @Test
    fun `el rumbo hacia el este es 90 grados`() {
        assertEquals(
            90.0,
            BearingCalculator().bearing(TripFixtures.at(0.0, 0.0), TripFixtures.at(100.0, 0.0)).value,
            0.5
        )
    }

    @Test
    fun `la distancia a la ruta usa el segmento mas cercano`() {
        val path = listOf(TripFixtures.at(0.0, 0.0), TripFixtures.at(100.0, 0.0))
        assertEquals(30.0, PolylineDistanceCalculator().distanceToPath(TripFixtures.at(50.0, 30.0), path).value, 0.5)
    }

    @Test
    fun `el remuestreo deja un punto cada 20 metros`() {
        val line = listOf(TripFixtures.at(0.0, 0.0), TripFixtures.at(100.0, 0.0))
        val resampled = TrajectoryResampler(20.0).resample(line)
        assertEquals(6, resampled.size)
        resampled.zipWithNext { a, b -> assertEquals(20.0, haversine.distance(a, b).value, 0.5) }
    }

    @Test
    fun `el remuestreo elimina los puntos de cuando esta quieto`() {
        val still = List(10) { TripFixtures.at(0.0, 0.0) } + TripFixtures.at(40.0, 0.0)
        assertEquals(3, TrajectoryResampler(20.0).resample(still).size)
    }

    @Test
    fun `la grilla reconoce que volvio a la misma celda`() {
        val grid = SpatialGrid(TripFixtures.ORIGIN, 20.0)
        grid.visit(0, TripFixtures.at(5.0, 5.0))
        grid.visit(1, TripFixtures.at(50.0, 5.0))
        assertEquals(listOf(0), grid.visit(2, TripFixtures.at(8.0, 9.0)))
    }
}
