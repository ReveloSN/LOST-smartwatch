package com.lost.domain.units

import com.lost.domain.model.units.Degrees
import com.lost.domain.model.units.Meters
import com.lost.domain.model.units.Probability
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class UnitsTest {
    @Test
    fun `una probabilidad fuera de 0 a 1 se rechaza`() {
        assertThrows<IllegalArgumentException> { Probability(1.2) }
        assertThrows<IllegalArgumentException> { Probability(-0.1) }
    }

    @Test
    fun `clamped recorta al rango valido`() {
        assertEquals(1.0, Probability.clamped(3.0).value)
        assertEquals(0.0, Probability.clamped(-3.0).value)
    }

    @Test
    fun `la diferencia angular toma el camino mas corto`() {
        assertEquals(20.0, Degrees(350.0).shortestDifferenceTo(Degrees(10.0)).value, 1e-9)
        assertEquals(180.0, Degrees(0.0).shortestDifferenceTo(Degrees(180.0)).value, 1e-9)
    }

    @Test
    fun `los grados se normalizan entre 0 y 360`() {
        assertEquals(270.0, Degrees(-90.0).normalized().value, 1e-9)
        assertEquals(30.0, Degrees(390.0).normalized().value, 1e-9)
    }

    @Test
    fun `los metros se suman y comparan`() {
        assertEquals(Meters(30.0), Meters(10.0) + Meters(20.0))
        assertTrue(Meters(5.0) < Meters(6.0))
    }
}
