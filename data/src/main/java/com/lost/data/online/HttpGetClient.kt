package com.lost.data.online

import java.io.IOException

/** Hace un GET y devuelve el cuerpo. Interfaz mínima para probar los servicios sin red. */
fun interface HttpGetClient {
    @Throws(IOException::class)
    fun get(url: String): ByteArray
}
