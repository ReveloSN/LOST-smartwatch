package com.lost.domain.map

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class WebMercatorTest {
    @Test
    fun `el origen cae en el centro del mundo`() {
        assertEquals(128.0, WebMercator.worldX(0.0, 0), 1e-9)
        assertEquals(128.0, WebMercator.worldY(0.0, 0), 1e-9)
    }

    @Test
    fun `los bordes del mundo son -180 y 180 de longitud`() {
        assertEquals(0.0, WebMercator.worldX(-180.0, 1), 1e-9)
        assertEquals(512.0, WebMercator.worldX(180.0, 1), 1e-9)
    }

    @Test
    fun `la Universidad Nacional cae en la tesela de OpenStreetMap correcta`() {
        val zoom = 16
        val x = WebMercator.tileIndex(WebMercator.worldX(-74.0852, zoom))
        val y = WebMercator.tileIndex(WebMercator.worldY(4.6387, zoom))
        assertEquals(TileKey(16, 19281, 31922), TileKey(zoom, x, y))
    }

    @Test
    fun `el norte esta arriba`() {
        assert(WebMercator.worldY(10.0, 5) < WebMercator.worldY(0.0, 5))
    }
}
