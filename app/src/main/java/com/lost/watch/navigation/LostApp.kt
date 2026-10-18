package com.lost.watch.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.wear.compose.material3.AppScaffold
import com.lost.designsystem.theme.LostTheme
import com.lost.watch.feature.assistance.AssistanceOverlay
import com.lost.watch.ui.LargeTextScale

/**
 * Raíz de la app: tema, texto grande, navegación y la capa de alerta.
 * La alerta no vive en el árbol de navegación: se superpone a cualquier pantalla.
 */
@Composable
fun LostApp(viewModel: RootViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LostTheme {
        LargeTextScale(isEnabled = state.isLargeTextEnabled) {
            AppScaffold {
                if (!state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        LostNavHost(isOnboardingCompleted = state.isOnboardingCompleted)
                        AssistanceOverlay()
                    }
                }
            }
        }
    }
}
