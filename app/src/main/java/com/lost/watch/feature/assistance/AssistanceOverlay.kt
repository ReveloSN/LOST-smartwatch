package com.lost.watch.feature.assistance

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Capa de alerta: aparece sobre cualquier pantalla y, al terminar, devuelve al usuario a donde estaba.
 * La transición es suave (fundido) para pasar del estado tranquilo a la asistencia sin sobresaltos.
 */
@Composable
fun AssistanceOverlay(viewModel: AssistanceViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    AnimatedContent(
        targetState = state,
        transitionSpec = {
            fadeIn(tween(SemanticTokens.Motion.TRANSITION_MILLIS)) togetherWith
                fadeOut(tween(SemanticTokens.Motion.TRANSITION_MILLIS))
        },
        contentKey = { it::class },
        label = "assistanceLayer"
    ) { target -> AssistanceLayer(target, viewModel::onEvent) }
}

@Composable
private fun AssistanceLayer(state: AssistanceUiState, onEvent: (AssistanceUiEvent) -> Unit) {
    when (state) {
        AssistanceUiState.Hidden -> Unit
        is AssistanceUiState.SoftAsk -> SoftAskAlert(onEvent)
        is AssistanceUiState.AskForHelp -> AskForHelpAlert(state, onEvent)
        is AssistanceUiState.Guiding -> GuidingAlert(state)
        is AssistanceUiState.SharingCountdown -> SharingCountdownAlert(state, onEvent)
        is AssistanceUiState.LocationShared -> LocationSharedAlert(state, onEvent)
        is AssistanceUiState.Acknowledged -> AcknowledgedAlert(state)
        AssistanceUiState.BackOnRoute -> BackOnRouteAlert(onEvent)
    }
}
