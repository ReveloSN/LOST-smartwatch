package com.lost.domain.model

/**
 * Persona que puede recibir la ubicación cuando LOST ayuda. El permiso es explícito
 * y revocable: [hasAcceptedInvitation] indica si ya aceptó.
 */
data class AuthorizedContact(
    val id: String,
    val name: String,
    val relationship: String,
    val phoneNumber: String,
    val hasAcceptedInvitation: Boolean
)
