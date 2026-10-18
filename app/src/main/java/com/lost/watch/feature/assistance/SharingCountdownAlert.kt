package com.lost.watch.feature.assistance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.organisms.ProbabilityArc
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R

/** F.4 · Cuenta regresiva cancelable: anillo que se vacía, número grande, sin rojo puro. */
@Composable
fun SharingCountdownAlert(state: AssistanceUiState.SharingCountdown, onEvent: (AssistanceUiEvent) -> Unit) {
    val fraction = state.secondsLeft.toFloat() / state.totalSeconds
    val contact = state.contactName ?: stringResource(R.string.contact_fallback_name)
    WatchScreenTemplate(ring = { ProbabilityArc(StatusTone.HELP, fraction) }) {
        LostIcon(
            Icons.Rounded.ShareLocation,
            null,
            tint = SemanticTokens.Status.help,
            size = ComponentTokens.Icon.large
        )
        LostText(stringResource(R.string.assist_sharing_in, contact), role = LostTextRole.CAPTION)
        LostText(state.secondsLeft.toString(), role = LostTextRole.DISPLAY)
        LostText(pluralStringResource(R.plurals.assist_seconds, state.secondsLeft), role = LostTextRole.CAPTION)
        LostSecondaryButton(stringResource(R.string.assist_cancel), { onEvent(AssistanceUiEvent.CancelSharing) })
    }
}

@LostDevicePreviews
@Composable
private fun SharingCountdownPreview() {
    LostTheme { SharingCountdownAlert(AssistanceUiState.SharingCountdown(5, 5, "Laura")) {} }
}
