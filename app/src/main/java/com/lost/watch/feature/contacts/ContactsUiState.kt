package com.lost.watch.feature.contacts

/** Contacto autorizado (o ninguno) y si "Quitar permiso" está armado. */
data class ContactsUiState(
    val name: String?,
    val relationship: String?,
    val phoneNumber: String?,
    val hasAcceptedInvitation: Boolean,
    val isRevokeArmed: Boolean
) {
    val hasContact: Boolean get() = name != null

    companion object {
        val EMPTY = ContactsUiState(null, null, null, hasAcceptedInvitation = false, isRevokeArmed = false)
    }
}
