package com.lost.watch.ui

import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RouteProjectorTest {
    private val projector = RouteProjector()
    private val habitual = listOf(GeoPoint(4.6000, -74.0800), GeoPoint(4.6000, -74.0750))

    @Test
    fun `todos los puntos quedan dentro del cuadro`() {
        val projected = projector.project(habitual, listOf(GeoPoint(4.5990, -74.0790)))
        (projected.habitual + projected.today).forEach {
            assertTrue(it.x in 0f..1f && it.y in 0f..1f, "fuera del cuadro: $it")
        }
    }

    @Test
    fun `el norte queda arriba`() {
        val projected = projector.project(listOf(GeoPoint(4.6000, -74.08), GeoPoint(4.6050, -74.08)), emptyList())
        assertTrue(projected.habitual[1].y < projected.habitual[0].y)
    }

    @Test
    fun `marca donde hoy se separa de la ruta de siempre`() {
        val onRoute = projector.project(habitual, listOf(GeoPoint(4.6000, -74.0790)))
        val away = projector.project(habitual, listOf(GeoPoint(4.6000, -74.0790), GeoPoint(4.5980, -74.0790)))
        assertNull(onRoute.divergence)
        assertNotNull(away.divergence)
        assertEquals(away.today[1], away.divergence)
    }
}
