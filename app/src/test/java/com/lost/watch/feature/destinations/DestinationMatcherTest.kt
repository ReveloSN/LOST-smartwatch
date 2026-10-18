package com.lost.watch.feature.destinations

import com.lost.domain.model.PlaceKind
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class DestinationMatcherTest {
    private val matcher = DestinationMatcher()
    private val items = listOf(
        DestinationItem("lab", "Laboratorio 3, Bloque C", PlaceKind.UNIVERSITY, isSuggested = true, isRecent = false),
        DestinationItem("lib", "Biblioteca Central", PlaceKind.LIBRARY, isSuggested = false, isRecent = true),
        DestinationItem("doc", "Consultorio 304", PlaceKind.HOSPITAL, isSuggested = false, isRecent = false)
    )

    @Test
    fun `encuentra el destino aunque diga mas palabras`() {
        assertEquals("lib", matcher.match("Llévame a la biblioteca", items)?.id)
    }

    @Test
    fun `ignora tildes y mayusculas`() {
        assertEquals("lab", matcher.match("LABORATÓRIO tres", items)?.id)
    }

    @Test
    fun `si no hay coincidencia no inventa un destino`() {
        assertNull(matcher.match("llévame a casa", items))
    }
}
