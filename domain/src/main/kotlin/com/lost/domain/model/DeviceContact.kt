package com.lost.domain.model

/** Un contacto guardado en el reloj (sincronizado desde el teléfono) que se puede autorizar. */
data class DeviceContact(val id: String, val name: String, val phoneNumber: String)
