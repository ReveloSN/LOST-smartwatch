package com.lost.watch.feature.contacts

/** Acciones sobre el contacto autorizado. */
sealed interface ContactsUiEvent {
    /** Primer toque arma, el segundo quita el permiso (pide confirmación con respeto). */
    data object RevokeTapped : ContactsUiEvent
}
