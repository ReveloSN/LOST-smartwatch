package com.lost.watch.feature.assistance

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.StatusIconBadge
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.domain.assistance.UserFeedback
import com.lost.watch.R

private const val POP_START_SCALE = 0.6f

/** V.3 / G.1 · "Entendido. LOST aprenderá de esto." Micro-feedback: check con rebote. */
@Composable
fun AcknowledgedAlert(state: AssistanceUiState.Acknowledged) {
    val scale = remember { Animatable(POP_START_SCALE) }
    LaunchedEffect(Unit) { scale.animateTo(1f, spring(dampingRatio = Spring.DampingRatioMediumBouncy)) }
    WatchScreenTemplate {
        StatusIconBadge(Icons.Rounded.Check, StatusTone.ON_ROUTE, Modifier.scale(scale.value), isFilled = true)
        LostText(stringResource(R.string.assist_understood), role = LostTextRole.QUESTION)
        LostText(stringResource(R.string.assist_will_learn), role = LostTextRole.BODY)
        if (state.feedback == UserFeedback.IM_FINE) {
            LostText(
                stringResource(R.string.assist_ask_less),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
        }
    }
}

@LostDevicePreviews
@Composable
private fun AcknowledgedPreview() {
    LostTheme { AcknowledgedAlert(AssistanceUiState.Acknowledged(UserFeedback.IM_FINE)) }
}
