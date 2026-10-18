package com.lost.watch.feature.assistance

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ShareLocation
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.organisms.AlertCard
import com.lost.designsystem.components.organisms.AlertContent
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.StatusTone
import com.lost.watch.R
import com.lost.watch.feature.errors.uiMessageText
import com.lost.watch.ui.UiMessage

/** F.4b · "Laura ya sabe dónde estás." Si no se pudo avisar, lo dice en tres partes. */
@Composable
fun LocationSharedAlert(state: AssistanceUiState.LocationShared, onEvent: (AssistanceUiEvent) -> Unit) {
    val contact = state.contactName ?: stringResource(R.string.contact_fallback_name)
    val content = state.error?.let { errorContent(it) } ?: AlertContent(
        Icons.Rounded.ShareLocation,
        stringResource(R.string.assist_shared_title, contact),
        stringResource(R.string.assist_shared_body),
        StatusTone.ON_ROUTE
    )
    WatchScreenTemplate {
        AlertCard(content)
        LostPrimaryButton(stringResource(R.string.assist_keep_guiding), { onEvent(AssistanceUiEvent.KeepGuiding) })
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
