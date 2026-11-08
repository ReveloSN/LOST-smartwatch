package com.lost.watch.feature.monitoring

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.detection.DeviationReason
import com.lost.domain.model.units.Meters
import com.lost.watch.R
import com.lost.watch.ui.explanationSentence

/** E.4 · IA explicable: en lenguaje humano, sin porcentajes. */
@Composable
fun ExplanationScreen(viewModel: MonitoringViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    ExplanationContent(state.reasons)
}

@Composable
private fun ExplanationContent(reasons: List<DeviationReason>) {
    WatchScreenTemplate {
        LostText(
            stringResource(R.string.explanation_title),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostText(explanationSentence(reasons), role = LostTextRole.QUESTION)
    }
}

@LostDevicePreviews
@Composable
private fun ExplanationPreview() {
    LostTheme {
        ExplanationContent(listOf(DeviationReason.LeftUsualRoute(Meters(150.0)), DeviationReason.RepeatedArea(3)))
    }
}
