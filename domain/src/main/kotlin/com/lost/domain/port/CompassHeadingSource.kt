package com.lost.domain.port

import kotlinx.coroutines.flow.Flow

/** Hacia dónde mira el reloj (grados desde el norte). Si no hay brújula, el flujo no emite nada. */
fun interface CompassHeadingSource {
    fun headings(): Flow<Double>
}
