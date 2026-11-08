package com.lost.data.demo

import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DemoPlaybackPaceTest {
    private val start = GeoPoint(1.2300, -77.2900, timestampMillis = 0L)

    @Test
    fun `caminando se reproduce diez veces mas rapido`() {
        val sevenMetersNorth = GeoPoint(1.2300 + 7.0 / METERS_PER_DEGREE, -77.2900, timestampMillis = 5_000L)
        assertEquals(500L, DemoPlaybackPace.delayMillis(start, sevenMetersNorth))
    }

    @Test
    fun `en bus se reproduce cuarenta veces mas rapido`() {
        val busStep = GeoPoint(1.2300 + 32.0 / METERS_PER_DEGREE, -77.2900, timestampMillis = 5_000L)
        assertEquals(125L, DemoPlaybackPace.delayMillis(start, busStep))
    }

    @Test
    fun `parado o con tiempos repetidos no espera de mas`() {
        assertEquals(500L, DemoPlaybackPace.delayMillis(start, start.copy(timestampMillis = 5_000L)))
        assertEquals(0L, DemoPlaybackPace.delayMillis(start, start))
    }

    private companion object {
        const val METERS_PER_DEGREE = 111_195.0
    }
}
