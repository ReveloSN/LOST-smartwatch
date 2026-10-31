package com.lost.domain.map

import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class MapViewportTest {
    private val center = GeoPoint(4.6387, -74.0852)
    private val zoom = 16
    private val viewport = MapViewport(
        zoom,
        WebMercator.worldX(center.longitude, zoom),
        WebMercator.worldY(center.latitude, zoom),
        widthPx = 300,
        heightPx = 300
    )

    @Test
    fun `el centro se dibuja en el centro`() {
        val fraction = viewport.fractionOf(center)
        assertEquals(0.5f, fraction.x, 1e-4f)
        assertEquals(0.5f, fraction.y, 1e-4f)
    }

    @Test
    fun `una ventana de reloj pide como mucho 9 teselas`() {
        val tiles = viewport.visibleTiles()
        assertTrue(tiles.size in 4..9, "teselas: ${tiles.size}")
        assertEquals(tiles.size, tiles.map { it.key }.toSet().size)
    }

    @Test
    fun `las teselas cubren toda la ventana`() {
        val tiles = viewport.visibleTiles()
        assertTrue(tiles.minOf { it.left } <= 0f && tiles.minOf { it.top } <= 0f)
        assertTrue(tiles.maxOf { it.left + it.width } >= 1f && tiles.maxOf { it.top + it.height } >= 1f)
    }

    @Test
    fun `la tesela del centro contiene al punto central`() {
        val middle = viewport.visibleTiles().first {
            0.5f in it.left..(it.left + it.width) &&
                0.5f in it.top..(it.top + it.height)
        }
        assertEquals(TileKey(16, 19281, 31922), middle.key)
    }
}
