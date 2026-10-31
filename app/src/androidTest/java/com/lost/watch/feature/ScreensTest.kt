package com.lost.watch.feature

import android.content.Context
import androidx.annotation.StringRes
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.lost.designsystem.theme.LostTheme
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.watch.R
import com.lost.watch.feature.home.HomeContent
import com.lost.watch.feature.home.HomeUiEvent
import com.lost.watch.feature.home.HomeUiState
import com.lost.watch.feature.monitoring.MonitoringContent
import com.lost.watch.feature.monitoring.MonitoringUiState
import com.lost.watch.feature.settings.SettingsContent
import com.lost.watch.feature.settings.SettingsUiEvent
import com.lost.watch.feature.settings.SettingsUiState
import com.lost.watch.ui.UiMessage
import com.lost.watch.ui.map.MapUiState
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test

/** Pantallas principales en el reloj: lo que se ve y lo que pasa al tocar, sin ViewModel ni red. */
class ScreensTest {
    @get:Rule
    val compose = createComposeRule()

    private fun text(@StringRes id: Int) = ApplicationProvider.getApplicationContext<Context>().getString(id)

    @Test
    fun inicioEnReposoEmpiezaUnRecorrido() {
        val events = mutableListOf<HomeUiEvent>()
        compose.setContent { LostTheme { HomeContent(HomeUiState.idle(isDemoAvailable = false), {}, events::add) } }
        compose.onNodeWithText(text(R.string.home_start_trip)).assertIsDisplayed().performClick()
        assertEquals(listOf(HomeUiEvent.StartTrip), events)
    }

    @Test
    fun inicioEnRecorridoAbreElDetalleDesdeElChipYTermina() {
        val events = mutableListOf<HomeUiEvent>()
        var openedMonitoring = false
        val state = HomeUiState.idle(false).copy(isMonitoring = true, ringProgress = 0.3f, routineName = "U")
        compose.setContent { LostTheme { HomeContent(state, { openedMonitoring = true }, events::add) } }
        compose.onNodeWithText(text(R.string.home_stop_trip)).performClick()
        assertEquals(listOf(HomeUiEvent.StopTrip), events)
        compose.onNodeWithText(text(state.status.label), substring = true).performClick()
        assertEquals(true, openedMonitoring)
    }

    @Test
    fun ajustesCambiaElTextoGrande() {
        val events = mutableListOf<SettingsUiEvent>()
        val state = SettingsUiState(
            Sensitivity.MEDIUM,
            VibrationIntensity.STRONG,
            false,
            35,
            isDemoAvailable = false,
            hasDeletedLearning = false
        )
        compose.setContent { LostTheme { SettingsContent(state, {}, events::add, {}) } }
        compose.onNodeWithText(text(R.string.settings_large_text)).performClick()
        assertEquals(listOf(SettingsUiEvent.ToggleLargeText), events)
    }

    @Test
    fun sinInternetMonitoreoMuestraElMiniMapaYQueHacer() {
        val noMap = MapUiState.Unavailable(
            UiMessage(R.string.error_map_what, R.string.error_connection_why, R.string.error_map_todo)
        )
        compose.setContent { LostTheme { MonitoringContent(MonitoringUiState.IDLE, noMap) {} } }
        compose.onNodeWithContentDescription(text(R.string.cd_route_comparison)).assertIsDisplayed()
        compose.onNodeWithText(text(R.string.error_map_todo)).assertIsDisplayed()
        compose.onNodeWithText(text(R.string.map_attribution)).assertDoesNotExist()
    }
}
