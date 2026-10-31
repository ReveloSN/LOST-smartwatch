package com.lost.watch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.foundation.hierarchicalFocusGroup
import androidx.wear.compose.material3.AppScaffold
import com.lost.designsystem.theme.LostTheme
import com.lost.watch.feature.assistance.AssistanceOverlay
import com.lost.watch.feature.assistance.AssistanceUiState
import com.lost.watch.feature.assistance.AssistanceViewModel
import com.lost.watch.ui.LargeTextScale

/**
 * Raíz de la app: tema, texto grande, navegación y la capa de alerta.
 * La alerta no vive en el árbol de navegación: se superpone a cualquier pantalla.
 */
@Composable
fun LostApp(viewModel: RootViewModel = hiltViewModel(), assistance: AssistanceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val alert by assistance.uiState.collectAsStateWithLifecycle()
    val isAlertVisible = alert != AssistanceUiState.Hidden
    LostTheme {
        LargeTextScale(isEnabled = state.isLargeTextEnabled) {
            AppScaffold {
                if (!state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Con una alerta encima, la corona es de la alerta; al cerrarla vuelve a la pantalla de abajo.
                        Box(Modifier.fillMaxSize().hierarchicalFocusGroup(active = !isAlertVisible)) {
                            LostNavHost(isOnboardingCompleted = state.isOnboardingCompleted)
                        }
                        Box(Modifier.fillMaxSize().hierarchicalFocusGroup(active = isAlertVisible)) {
                            AssistanceOverlay(assistance)
                        }
                    }
                }
            }
        }
    }
}
