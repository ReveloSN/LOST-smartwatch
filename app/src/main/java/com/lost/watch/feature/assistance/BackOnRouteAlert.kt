package com.lost.watch.feature.assistance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.organisms.BreathingStatusRing
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import kotlinx.coroutines.delay

private const val VISIBLE_MILLIS = 3_000L
private const val FULL_RING = 1f

/** F.5 · De vuelta en tu ruta: el anillo vuelve a aqua y respira. Se cierra sola. */
@Composable
fun BackOnRouteAlert(onEvent: (AssistanceUiEvent) -> Unit) {
    LaunchedEffect(Unit) {
        delay(VISIBLE_MILLIS)
        onEvent(AssistanceUiEvent.DismissBackOnRoute)
    }
    WatchScreenTemplate(ring = { BreathingStatusRing(StatusTone.ON_ROUTE, FULL_RING) }) {
        LostIcon(
            Icons.Rounded.CheckCircle,
            null,
            tint = SemanticTokens.Status.onRoute,
            size = ComponentTokens.Icon.large
        )
        LostText(stringResource(R.string.assist_back_on_route), role = LostTextRole.QUESTION)
    }
}

@LostDevicePreviews
@Composable
private fun BackOnRoutePreview() {
    LostTheme { BackOnRouteAlert {} }
}
