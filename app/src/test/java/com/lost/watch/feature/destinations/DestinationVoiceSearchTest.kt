package com.lost.watch.feature.destinations

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.PlaceSuggestion
import com.lost.domain.model.UserSettings
import com.lost.domain.port.DestinationSearchService
import com.lost.domain.port.DestinationWriter
import com.lost.domain.usecase.ObserveSettingsUseCase
import com.lost.domain.usecase.SaveDestinationUseCase
import com.lost.domain.usecase.SearchPlacesUseCase
import com.lost.watch.R
import com.lost.watch.ui.LostErrorToUiMessageMapper
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class DestinationVoiceSearchTest {
    private val library = PlaceSuggestion("Biblioteca Central", "Teusaquillo", GeoPoint(4.6355, -74.0830))
    private val sentQueries = mutableListOf<String>()
    private val saved = mutableListOf<Destination>()

    private fun search(
        isRealMapEnabled: Boolean = true,
        service: DestinationSearchService = DestinationSearchService { query ->
            sentQueries += query
            LostResult.Success(listOf(library))
        }
    ) = DestinationVoiceSearch(
        matcher = DestinationMatcher(),
        observeSettings = ObserveSettingsUseCase {
            flowOf(UserSettings.DEFAULT.copy(isRealMapEnabled = isRealMapEnabled))
        },
        searchPlaces = SearchPlacesUseCase(service),
        saveDestination = SaveDestinationUseCase(
            object : DestinationWriter {
                override suspend fun save(destination: Destination) {
                    saved += destination
                }
            }
        ) { 0L },
        ids = { "nuevo" },
        errors = LostErrorToUiMessageMapper()
    )

    @Test
    fun `un destino guardado se abre sin usar la red`() {
        val home = DestinationItem("casa", "Casa", PlaceKind.HOME, isSuggested = false, isRecent = true)
        assertEquals("casa", search().matchSaved("llévame a casa", listOf(home))?.matchedId)
        assertTrue(sentQueries.isEmpty())
    }

    @Test
    fun `busca en OpenStreetMap solo el nombre del lugar`() = runTest {
        val state = search().searchOnline("Llévame a la biblioteca central")
        assertEquals(listOf("biblioteca central"), sentQueries)
        assertEquals(listOf(library), state.results)
    }

    @Test
    fun `con el mapa real apagado no sale nada del reloj`() = runTest {
        val state = search(isRealMapEnabled = false).searchOnline("biblioteca central")
        assertTrue(sentQueries.isEmpty())
        assertEquals("biblioteca central", state.notFoundQuery)
    }

    @Test
    fun `sin conexion muestra un mensaje de tres partes`() = runTest {
        val state = search { LostResult.Failure(LostError.SearchUnavailable) }.searchOnline("biblioteca")
        assertEquals(R.string.error_search_what, state.error?.whatHappened)
        assertEquals(R.string.error_search_todo, state.error?.whatToDo)
    }

    @Test
    fun `elegir un lugar lo guarda con sus coordenadas`() = runTest {
        val state = search().choose(library)
        assertEquals("nuevo", state.matchedId)
        assertEquals(library.location, saved.single().location)
        assertEquals("Biblioteca Central", saved.single().name)
    }
}
