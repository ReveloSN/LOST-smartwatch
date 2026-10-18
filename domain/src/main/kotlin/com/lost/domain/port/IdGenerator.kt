package com.lost.domain.port

/** Genera identificadores únicos (UUID en producción, secuenciales en pruebas). */
fun interface IdGenerator {
    fun newId(): String
}
