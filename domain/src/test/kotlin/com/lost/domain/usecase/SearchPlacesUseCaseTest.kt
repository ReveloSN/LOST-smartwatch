package com.lost.domain.usecase

import com.lost.domain.error.LostResult
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceSuggestion
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class SearchPlacesUseCaseTest {
    private val sent = mutableListOf<String>()
    private val useCase = SearchPlacesUseCase { query ->
        sent += query
        LostResult.Success(listOf(PlaceSuggestion("Biblioteca", "Bogotá", GeoPoint(4.6, -74.0))))
    }

    @Test
    fun `envia solo el texto recortado`() = runTest {
        useCase("  biblioteca central ")
        assertEquals(listOf("biblioteca central"), sent)
    }

    @Test
    fun `un texto muy corto no sale del reloj`() = runTest {
        assertTrue(useCase("a").valueOrNull()!!.isEmpty())
        assertTrue(sent.isEmpty())
    }
}
