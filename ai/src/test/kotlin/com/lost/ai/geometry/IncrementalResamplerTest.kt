package com.lost.ai.geometry

import com.lost.ai.fixtures.SyntheticWalk
import com.lost.ai.fixtures.TripFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class IncrementalResamplerTest {
    private val full = TrajectoryResampler(20.0)
    private val walk = SyntheticWalk().to(300.0, 0.0).to(300.0, -150.0).square(60.0, 2).stay(30).points()

    @Test
    fun `punto a punto da lo mismo que remuestrear todo`() {
        val incremental = full.incremental()
        (1..walk.size).forEach { count ->
            val prefix = walk.take(count)
            assertEquals(full.resample(prefix), incremental.resample(prefix), "con $count puntos")
        }
    }

    @Test
    fun `un recorrido distinto empieza de cero`() {
        val incremental = full.incremental()
        incremental.resample(walk)
        val other = TripFixtures.habitualWalk().points()
        assertEquals(full.resample(other), incremental.resample(other))
    }
}
