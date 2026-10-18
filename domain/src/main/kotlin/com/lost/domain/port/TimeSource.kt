package com.lost.domain.port

/** Reloj inyectable para que las pruebas controlen el tiempo. */
fun interface TimeSource {
    fun nowMillis(): Long
}
