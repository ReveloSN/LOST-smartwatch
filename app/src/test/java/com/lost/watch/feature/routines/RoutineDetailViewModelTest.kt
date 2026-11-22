package com.lost.watch.feature.routines

import androidx.lifecycle.SavedStateHandle
import com.lost.domain.usecase.ObserveRoutinesUseCase
import com.lost.watch.AppFixtures
import com.lost.watch.MainDispatcherExtension
import com.lost.watch.navigation.LostDestination
import com.lost.watch.ui.map.MapUiState
import com.lost.watch.ui.map.MapUiStateFlow
import com.lost.watch.ui.map.RouteMapPicture
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(MainDispatcherExtension::class)
class RoutineDetailViewModelTest {
    private val routine = AppFixtures.routine
    private val loadedMaps = MutableSharedFlow<MapUiState>()

    // Perezoso: el ViewModel debe nacer cuando la extensión ya reemplazó Dispatchers.Main.
    private val viewModel by lazy {
        RoutineDetailViewModel(
            savedStateHandle = SavedStateHandle(mapOf(LostDestination.RoutineDetail.ARG_ROUTINE_ID to routine.id)),
            observeRoutines = mockk<ObserveRoutinesUseCase> {
                every { this@mockk.invoke() } returns flowOf(listOf(routine))
            },
            actions = mockk(),
            mapper = RoutineItemMapper(),
            mapStates = mockk<MapUiStateFlow> { every { of(any()) } returns loadedMaps }
        )
    }

    @Test
    fun `el detalle se ve con el mapa simple mientras cargan las calles`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()

        assertEquals(routine.id, viewModel.uiState.value.item?.id)
        assertEquals(routine.path, viewModel.uiState.value.path)
        assertEquals(MapUiState.Simple, viewModel.uiState.value.map)
    }

    @Test
    fun `cuando llegan las calles reemplazan al mapa simple`() = runTest {
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.uiState.collect {} }
        runCurrent()
        val real = MapUiState.Real(RouteMapPicture(emptyList(), emptyList(), emptyList()))

        loadedMaps.emit(real)
        runCurrent()

        assertEquals(real, viewModel.uiState.value.map)
    }
}
