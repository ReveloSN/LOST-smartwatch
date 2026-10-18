package com.lost.ai.sequence

import com.lost.ai.fixtures.TripFixtures.at
import com.lost.ai.geometry.TrajectoryResampler
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SequenceComparatorTest {
    private val resampler = TrajectoryResampler(20.0)
    private val reference = resampler.resample(listOf(at(0.0, 0.0), at(400.0, 0.0)))
    private val shifted = resampler.resample(listOf(at(0.0, 50.0), at(400.0, 50.0)))
    private val half = resampler.resample(listOf(at(0.0, 0.0), at(200.0, 0.0)))
    private val comparators = listOf(DtwSequenceComparator(0.25, 5), FrechetSequenceComparator())

    @Test
    fun `secuencias identicas cuestan cero`() {
        comparators.forEach { assertEquals(0.0, it.compare(reference, reference, isPartial = false), 0.01) }
    }

    @Test
    fun `una ruta paralela a 50 m cuesta unos 50 m`() {
        comparators.forEach { assertEquals(50.0, it.compare(reference, shifted, isPartial = false), 1.0) }
    }

    @Test
    fun `un recorrido en curso se compara contra el mejor prefijo`() {
        comparators.forEach {
            assertEquals(0.0, it.compare(reference, half, isPartial = true), 0.5)
            assertTrue(it.compare(reference, half, isPartial = false) > 10.0)
        }
    }

    @Test
    fun `dtw tolera caminar mas lento en un tramo`() {
        val slow =
            listOf(at(0.0, 0.0)) + List(5) { at(10.0, 0.0) } + resampler.resample(listOf(at(20.0, 0.0), at(400.0, 0.0)))
        assertTrue(DtwSequenceComparator(0.25, 5).compare(reference, slow, isPartial = false) < 10.0)
    }

    @Test
    fun `el avance sobre la ruta va de 0 a 1`() {
        val locator = RouteProgressLocator()
        assertEquals(0.0, locator.progress(at(0.0, 0.0), reference), 0.01)
        assertEquals(0.5, locator.progress(at(200.0, 5.0), reference), 0.01)
        assertEquals(1.0, locator.progress(at(410.0, 0.0), reference), 0.01)
    }
}
