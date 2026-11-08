package com.lost.domain.session

import com.lost.domain.port.CompassHeadingSource
import com.lost.domain.usecase.ObserveCompassHeadingUseCase
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ArrowBearingTest {
    @Test
    fun `sin brujula la flecha apunta al rumbo absoluto`() {
        assertEquals(45.0, ArrowBearing.onScreen(45.0, null))
    }

    @Test
    fun `si el destino esta adelante la flecha apunta arriba`() {
        assertEquals(0.0, ArrowBearing.onScreen(90.0, 90.0))
    }

    @Test
    fun `si mira al norte y el destino esta al oeste la flecha apunta a la izquierda`() {
        assertEquals(270.0, ArrowBearing.onScreen(270.0, 0.0))
        assertEquals(270.0, ArrowBearing.onScreen(0.0, 90.0))
    }

    @Test
    fun `la brujula solo se escucha mientras guia`() = runTest {
        var subscriptions = 0
        val compass = CompassHeadingSource {
            subscriptions++
            flowOf(30.0)
        }
        val values = ObserveCompassHeadingUseCase(compass)(flowOf(false, true)).take(3).toList()
        assertEquals(listOf(null, null, 30.0), values)
        assertEquals(1, subscriptions)
    }
}
