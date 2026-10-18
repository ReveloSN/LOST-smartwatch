package com.lost.data.demo

import app.cash.turbine.test
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class SimulatedLocationSourceTest {
    private val gpx = """
        <gpx><trk><trkseg>
          <trkpt lat="1.0" lon="1.0"><time>2026-10-12T11:40:00Z</time></trkpt>
          <trkpt lat="1.1" lon="1.1"><time>2026-10-12T11:40:10Z</time></trkpt>
          <trkpt lat="1.2" lon="1.2"><time>2026-10-12T11:40:20Z</time></trkpt>
        </trkseg></trk></gpx>
    """.trimIndent()

    @Test
    fun `sin escenario no emite nada y al reproducir conserva la separacion real de tiempos`() = runTest {
        val selector = DemoScenarioSelector()
        val source = SimulatedLocationSource(
            opener = { gpx.byteInputStream() },
            reader = GpxRouteReader(),
            selector = selector,
            timeSource = { 1_000_000L }
        )
        source.locations().test {
            expectNoEvents()
            selector.play(DemoScenario.NORMAL_ROUTINE)
            val first = awaitItem()
            val second = awaitItem()
            val third = awaitItem()
            assertEquals(1_000_000L, first.timestampMillis)
            assertEquals(10_000L, second.timestampMillis - first.timestampMillis)
            assertEquals(1.2, third.latitude, 1e-9)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
