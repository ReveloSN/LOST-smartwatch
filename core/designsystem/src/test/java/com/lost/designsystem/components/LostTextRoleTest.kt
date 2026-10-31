package com.lost.designsystem.components

import com.lost.designsystem.components.atoms.LostTextRole
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LostTextRoleTest {
    @Test
    fun `con el tamano normal cada rol conserva sus lineas`() {
        LostTextRole.entries.forEach { assertEquals(it.maxLines, it.maxLinesAt(1f), it.name) }
    }

    @Test
    fun `con texto grande los textos de varias lineas ganan lineas en vez de cortarse`() {
        assertEquals(3, LostTextRole.CAPTION.maxLinesAt(1.3f))
        assertEquals(4, LostTextRole.BODY.maxLinesAt(1.3f))
        assertEquals(4, LostTextRole.QUESTION.maxLinesAt(1.3f))
    }

    @Test
    fun `los numeros y el display siguen en una sola linea`() {
        assertEquals(1, LostTextRole.DISPLAY.maxLinesAt(1.3f))
        assertEquals(1, LostTextRole.NUMBER.maxLinesAt(1.3f))
    }
}
