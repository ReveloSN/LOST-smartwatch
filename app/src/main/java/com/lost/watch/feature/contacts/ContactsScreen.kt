package com.lost.watch.feature.contacts

import android.content.Intent
import android.net.Uri
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.InitialAvatar
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostSecondaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.atoms.SecondaryButtonStyle
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/** 04.3 · Contacto autorizado con permiso revocable. */
@Composable
fun ContactsScreen(viewModel: ContactsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    ContactsContent(
        state = state,
        onCall = { number ->
            runCatching { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$number"))) }
        },
        onEvent = viewModel::onEvent
    )
}

@Composable
private fun ContactsContent(state: ContactsUiState, onCall: (String) -> Unit, onEvent: (ContactsUiEvent) -> Unit) {
    WatchScreenTemplate {
        if (!state.hasContact) {
            LostIcon(Icons.Rounded.Group, null, tint = SemanticTokens.Status.onRoute)
            LostText(stringResource(R.string.contact_empty), role = LostTextRole.TITLE)
            LostText(
                stringResource(R.string.error_no_contact_todo),
                role = LostTextRole.CAPTION,
                color = SemanticTokens.Text.secondary
            )
            return@WatchScreenTemplate
        }
        InitialAvatar(state.name.orEmpty())
        LostText(state.name.orEmpty(), role = LostTextRole.QUESTION)
        LostText(stringResource(R.string.contact_authorized, state.relationship.orEmpty()), role = LostTextRole.CAPTION)
        val note = if (state.hasAcceptedInvitation) R.string.contact_sees_location else R.string.contact_pending
        LostText(stringResource(note), role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        state.phoneNumber?.takeIf { it.isNotBlank() }?.let { number ->
            LostPrimaryButton(stringResource(R.string.contact_call), { onCall(number) })
        }
        val revokeLabel = if (state.isRevokeArmed) R.string.contact_revoke_confirm else R.string.contact_revoke
        LostSecondaryButton(stringResource(revokeLabel), {
            onEvent(ContactsUiEvent.RevokeTapped)
        }, style = SecondaryButtonStyle.TEXT_ONLY)
    }
}

@LostDevicePreviews
@Composable
private fun ContactsPreview() {
    LostTheme { ContactsContent(ContactsUiState("Laura", "Hermana", "+57300", true, false), {}, {}) }
}

@LostDevicePreviews
@Composable
private fun ContactsEmptyPreview() {
    LostTheme { ContactsContent(ContactsUiState.EMPTY, {}, {}) }
}
