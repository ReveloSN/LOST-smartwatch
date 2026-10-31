package com.lost.watch.feature.learning

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.organisms.SegmentState
import com.lost.designsystem.components.organisms.SegmentedProgressRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.PlaceKind
import com.lost.watch.R
import com.lost.watch.ui.PlaceIcons

/** B.1 · Aprendiendo tu rutina: un segmento del anillo por cada repetición. */
@Composable
fun LearningScreen(viewModel: LearningViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state?.let { LearningContent(it) { viewModel.onEvent(LearningUiEvent.ConfirmNow) } }
}

@Composable
private fun LearningContent(state: LearningUiState, onConfirm: () -> Unit) {
    val segments = List(state.requiredRepetitions) { segmentFor(it, state.timesObserved) }
    WatchScreenTemplate(ring = { SegmentedProgressRing(segments) }) {
        LostIcon(PlaceIcons.of(state.placeKind), null, tint = SemanticTokens.Status.onRoute)
        LostText(
            stringResource(R.string.learning_progress, state.timesObserved, state.requiredRepetitions),
            role = LostTextRole.NUMBER
        )
        if (state.remaining > 0) {
            LostText(
                pluralStringResource(R.plurals.learning_remaining, state.remaining, state.remaining),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
            LostSecondaryButton(
                stringResource(R.string.routine_save),
                onConfirm,
                style = SecondaryButtonStyle.TEXT_ONLY
            )
        }
    }
}

private fun segmentFor(index: Int, timesObserved: Int) =
    if (index < timesObserved) SegmentState.DONE else SegmentState.EMPTY

@LostDevicePreviews
@Composable
private fun LearningPreview() {
    LostTheme { LearningContent(LearningUiState(4, 5, PlaceKind.UNIVERSITY), onConfirm = {}) }
}
