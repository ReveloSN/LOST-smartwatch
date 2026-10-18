package com.lost.data.demo

import java.io.File
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class GpxRouteReaderTest {
    private val reader = GpxRouteReader()

    @Test
    fun `lee latitud longitud y hora de cada punto`() {
        val gpx = """
            <gpx><trk><trkseg>
              <trkpt lat="4.6097" lon="-74.0817"><time>2026-10-12T11:40:00Z</time></trkpt>
              <trkpt lat="4.6098" lon="-74.0816"><time>2026-10-12T11:40:05Z</time></trkpt>
            </trkseg></trk></gpx>
        """.trimIndent()
        val points = reader.read(gpx.byteInputStream())
        assertEquals(2, points.size)
        assertEquals(4.6097, points[0].latitude, 1e-9)
        assertEquals(5_000L, points[1].timestampMillis - points[0].timestampMillis)
    }

    @Test
    fun `todos los recorridos de la demo se pueden leer`() {
        DemoScenario.entries.forEach { scenario ->
            val file = File("../sample-routes/${scenario.assetName}")
            val points = file.inputStream().use(reader::read)
            assertTrue(points.size > 50, "${scenario.assetName} tiene ${points.size} puntos")
            assertTrue(points.zipWithNext().all { (a, b) -> b.timestampMillis > a.timestampMillis })
        }
    }
}
