package com.lost.watch.feature.assistance

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.StatusIconBadge
import com.lost.designsystem.components.organisms.AlertCard
import com.lost.designsystem.components.organisms.AlertContent
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import com.lost.watch.feature.errors.uiMessageText
import com.lost.watch.ui.UiMessage

/**
 * F.4b · "Laura ya sabe dónde estás.", como en el mockup: icono en el tono de ayuda dentro de un
 * círculo, sin tarjeta. Si no se pudo avisar, lo dice en tres partes dentro de una tarjeta de alerta.
 */
@Composable
fun LocationSharedAlert(state: AssistanceUiState.LocationShared, onEvent: (AssistanceUiEvent) -> Unit) {
    val contact = state.contactName ?: stringResource(R.string.contact_fallback_name)
    WatchScreenTemplate {
        val error = state.error
        if (error != null) AlertCard(errorContent(error)) else SharedContent(contact)
        LostPrimaryButton(stringResource(R.string.assist_keep_guiding), { onEvent(AssistanceUiEvent.KeepGuiding) })
    }
}

@Composable
private fun SharedContent(contact: String) {
    Column(
        modifier = Modifier.semantics(mergeDescendants = true) { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.xs)
    ) {
        StatusIconBadge(Icons.Rounded.ShareLocation, StatusTone.HELP)
        LostText(stringResource(R.string.assist_shared_title, contact), role = LostTextRole.QUESTION)
        LostText(
            stringResource(R.string.assist_shared_body),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}

@Composable
private fun errorContent(message: UiMessage): AlertContent {
    val text = uiMessageText(message)
    return AlertContent(
        Icons.Rounded.WifiOff,
        text.whatHappened,
        listOfNotNull(text.why, text.whatToDo).joinToString(" "),
        StatusTone.DEVIATION
    )
}

@LostDevicePreviews
@Composable
private fun LocationSharedPreview() {
    LostTheme { LocationSharedAlert(AssistanceUiState.LocationShared("Laura", null)) {} }
}

@LostDevicePreviews
@Composable
private fun LocationShareFailedPreview() {
    val error = UiMessage(R.string.error_connection_what, R.string.error_connection_why, R.string.error_connection_todo)
    LostTheme { LocationSharedAlert(AssistanceUiState.LocationShared("Laura", error)) {} }
}
