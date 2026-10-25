package com.lost.data.online

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class OnlineParsersTest {
    @Test
    fun `nominatim da nombre corto, zona y coordenadas`() {
        val json = """
            [{"name":"Biblioteca Central","lat":"4.6355","lon":"-74.0830",
              "display_name":"Biblioteca Central, Carrera 45, Teusaquillo, Bogotá, Colombia"}]
        """.trimIndent()
        val place = NominatimResponseParser().parse(json).single()
        assertEquals("Biblioteca Central", place.name)
        assertEquals("Carrera 45, Teusaquillo", place.detail)
        assertEquals(4.6355, place.location.latitude, 1e-9)
        assertEquals(-74.0830, place.location.longitude, 1e-9)
    }

    @Test
    fun `nominatim sin nombre usa la primera parte de la direccion`() {
        val json = """[{"name":"","lat":"1","lon":"2","display_name":"Calle 45, Bogotá"}]"""
        assertEquals("Calle 45", NominatimResponseParser().parse(json).single().name)
    }

    @Test
    fun `osrm convierte lon lat en puntos del recorrido`() {
        val json = """
            {"code":"Ok","routes":[{"geometry":{"type":"LineString",
              "coordinates":[[-74.0760,4.6320],[-74.0800,4.6340]]}}]}
        """.trimIndent()
        val route = OsrmRouteParser().parse(json)
        assertEquals(2, route.size)
        assertEquals(4.6320, route.first().latitude, 1e-9)
        assertEquals(-74.0800, route.last().longitude, 1e-9)
    }

    @Test
    fun `osrm sin ruta devuelve lista vacia`() {
        assertTrue(OsrmRouteParser().parse("""{"code":"NoRoute","routes":[]}""").isEmpty())
    }
}
