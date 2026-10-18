package com.lost.domain.session

import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.fakes.DomainFixtures
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RecoveryGraceTest {
    private val grace = RecoveryGrace()
    private val worried = DomainFixtures.result(AssistanceLevel.HIGH)

    @Test
    fun `sin recuperar la ruta no cambia nada`() {
        assertEquals(worried, grace.apply(worried, 10.0))
    }

    @Test
    fun `tras recuperar la ruta y mientras siga cerca lo anterior se perdona`() {
        grace.start()
        val forgiven = grace.apply(worried, 10.0)
        assertEquals(AssistanceLevel.LOW, forgiven.level)
        assertTrue(forgiven.reasons.isEmpty())
    }

    @Test
    fun `si se vuelve a salir la gracia termina`() {
        grace.start()
        assertEquals(worried, grace.apply(worried, 120.0))
        assertEquals(worried, grace.apply(worried, 10.0))
    }
}
