package com.lost.watch.ui.map

import com.lost.domain.model.GeoPoint
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MapFocusTest {
    /** Una línea hacia el norte con un punto cada 100 m. */
    private fun northward(meters: Int) = (0..meters step STEP).map { GeoPoint(1.16 + it / METERS_PER_DEGREE, -77.29) }

    @Test
    fun `una ruta corta se ve entera`() {
        val route = northward(1_500)
        assertNull(MapFocus.around(route, route.take(5)))
    }

    @Test
    fun `en una ruta larga mira una ventana de 1 km centrada en la persona`() {
        val route = northward(11_000)
        val current = route[60]
        val focus = requireNotNull(MapFocus.around(route, route.take(61)))
        val north = focus.maxOf { it.latitude }
        val south = focus.minOf { it.latitude }
        assertEquals(1_000.0, (north - south) * METERS_PER_DEGREE, 1.0)
        assertEquals(current.latitude, (north + south) / 2, 1e-9)
        assertEquals(current.longitude, (focus.maxOf { it.longitude } + focus.minOf { it.longitude }) / 2, 1e-9)
    }

    @Test
    fun `sin recorrido de hoy no hay foco`() {
        assertNull(MapFocus.around(northward(11_000), emptyList()))
    }

    private companion object {
        const val STEP = 100
        const val METERS_PER_DEGREE = 111_195.0
    }
}
