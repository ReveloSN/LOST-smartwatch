package com.lost.domain.map

import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ViewportFitterTest {
    private val fitter = ViewportFitter()
    private val frame = ViewportFrame(widthPx = 300, heightPx = 300, marginFraction = 0.2)

    @Test
    fun `sin puntos no hay encuadre`() {
        assertNull(fitter.fit(emptyList(), frame))
    }

    @Test
    fun `todos los puntos quedan dentro del margen`() {
        val route = listOf(GeoPoint(4.6300, -74.0760), GeoPoint(4.6340, -74.0800), GeoPoint(4.6387, -74.0852))
        val viewport = requireNotNull(fitter.fit(route, frame))
        route.map(viewport::fractionOf).forEach { p ->
            assertTrue(p.x in 0.2f..0.8f && p.y in 0.2f..0.8f, "fuera del margen: $p")
        }
    }

    @Test
    fun `elige el zoom mas cercano que todavia cabe`() {
        val route = listOf(GeoPoint(4.6300, -74.0760), GeoPoint(4.6387, -74.0852))
        val viewport = requireNotNull(fitter.fit(route, frame))
        val closer = ViewportFitter(minZoom = viewport.zoom + 1, maxZoom = viewport.zoom + 1).fit(route, frame)!!
        val outside = route.map(closer::fractionOf).any { it.x !in 0.2f..0.8f || it.y !in 0.2f..0.8f }
        assertTrue(outside, "con un zoom más cerca debería salirse")
    }

    @Test
    fun `un solo punto usa el zoom maximo`() {
        assertEquals(17, fitter.fit(listOf(GeoPoint(4.6, -74.0)), frame)?.zoom)
    }
}
