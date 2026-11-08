package com.lost.watch.feature.contacts

import android.Manifest
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Group
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostPrimaryButton
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.domain.model.DeviceContact
import com.lost.watch.R
import com.lost.watch.ui.rememberPermissionRequest

/**
 * Elegir contacto: los contactos del reloj (sincronizados desde el teléfono), con el porqué del permiso antes de
 * pedirlo. En Wear OS no hay un selector de contactos del sistema, por eso la lista es propia.
 */
@Composable
fun ContactPickerScreen(onChosen: () -> Unit, viewModel: ContactPickerViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val requestContacts = rememberPermissionRequest(arrayOf(Manifest.permission.READ_CONTACTS)) {
        viewModel.onEvent(ContactPickerUiEvent.PermissionAnswered)
    }
    // Si el permiso se dio en los ajustes del reloj, al volver se carga la lista.
    LifecycleResumeEffect(state) {
        if (state == ContactPickerUiState.NeedsPermission) viewModel.onEvent(ContactPickerUiEvent.PermissionAnswered)
        onPauseOrDispose {}
    }
    LaunchedEffect(state) { if (state == ContactPickerUiState.Chosen) onChosen() }
    ContactPickerContent(
        state = state,
        onAllow = requestContacts,
        onChoose = { viewModel.onEvent(ContactPickerUiEvent.Choose(it)) }
    )
}

@Composable
private fun ContactPickerContent(state: ContactPickerUiState, onAllow: () -> Unit, onChoose: (DeviceContact) -> Unit) {
    when (state) {
        ContactPickerUiState.NeedsPermission -> PermissionRationale(onAllow)
        is ContactPickerUiState.Contacts -> ContactList(state.contacts, onChoose)
        ContactPickerUiState.Loading, ContactPickerUiState.Chosen -> Unit
    }
}

/** Qué pasó · por qué · qué hacer, antes de que aparezca el diálogo del sistema. */
@Composable
private fun PermissionRationale(onAllow: () -> Unit) {
    WatchScreenTemplate {
        LostIcon(Icons.Rounded.Group, null, tint = SemanticTokens.Status.onRoute)
        LostText(stringResource(R.string.contact_picker_why), role = LostTextRole.TITLE)
        LostText(
            stringResource(R.string.contact_picker_privacy),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
        LostPrimaryButton(stringResource(R.string.contact_picker_allow), onAllow)
    }
}

@Composable
private fun ContactList(contacts: List<DeviceContact>, onChoose: (DeviceContact) -> Unit) {
    val empty = stringResource(R.string.contact_picker_empty)
    ListTemplate(title = stringResource(R.string.contact_choose)) {
        if (contacts.isEmpty()) {
            item(key = "empty") { LostText(empty, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary) }
        }
        contacts.forEach { contact ->
            item(key = contact.id) {
                LostListItem(ListItemContent(Icons.Rounded.Person, contact.name, contact.phoneNumber), {
                    onChoose(contact)
                })
            }
        }
    }
}

@LostDevicePreviews
@Composable
private fun ContactPickerListPreview() {
    val contacts =
        listOf(DeviceContact("1", "Laura", "+57 300 000 0000"), DeviceContact("2", "Papá", "+57 301 000 0000"))
    LostTheme { ContactPickerContent(ContactPickerUiState.Contacts(contacts), {}, {}) }
}

@LostDevicePreviews
@Composable
private fun ContactPickerPermissionPreview() {
    LostTheme { ContactPickerContent(ContactPickerUiState.NeedsPermission, {}, {}) }
}
