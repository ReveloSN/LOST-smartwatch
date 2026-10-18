package com.lost.watch.ui

import com.lost.designsystem.tokens.ComponentTokens
import com.lost.domain.detection.AssistanceLevel
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Reglas de accesibilidad de la sección 9.5 que se pueden comprobar sin pantalla. */
class AccessibilityRulesTest {
    @Test
    fun `el estado nunca se comunica solo con color`() {
        val presentations = AssistanceLevel.entries.map(StatusPresentation::of)
        assertEquals(presentations.size, presentations.map { it.icon }.toSet().size, "cada estado tiene su icono")
        assertEquals(presentations.size, presentations.map { it.label }.toSet().size, "cada estado tiene su texto")
        assertEquals(presentations.size, AssistanceLevel.entries.map { ToneResolver().resolve(it).haptic }.toSet().size)
    }

    @Test
    fun `las areas tactiles miden al menos 48 dp`() {
        assertTrue(ComponentTokens.Touch.minTarget.value >= 48f)
        assertTrue(ComponentTokens.PrimaryButton.height.value >= 48f)
        assertTrue(ComponentTokens.SecondaryButton.height.value >= 48f)
    }

    @Test
    fun `la flecha se describe con el rumbo en palabras`() {
        assertEquals(CompassDirection.NORTH, CompassDirection.of(0f))
        assertEquals(CompassDirection.NORTH, CompassDirection.of(350f))
        assertEquals(CompassDirection.NORTH_EAST, CompassDirection.of(40f))
        assertEquals(CompassDirection.SOUTH, CompassDirection.of(-180f))
        assertEquals(CompassDirection.WEST, CompassDirection.of(275f))
    }
}
