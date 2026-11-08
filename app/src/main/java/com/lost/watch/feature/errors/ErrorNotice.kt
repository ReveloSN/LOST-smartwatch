package com.lost.watch.feature.errors

import com.lost.watch.ui.UiMessage

/** Qué puede hacer la persona desde la pantalla de error: ninguna pantalla de error queda sin salida. */
enum class ErrorRemedy {
    /** Pedir el permiso de ubicación (o abrir los ajustes si Android ya no lo pregunta). */
    REQUEST_LOCATION,

    /** Es solo un aviso: "Entendido" y sigue a Inicio. */
    ACKNOWLEDGE
}

/** Un problema del reloj con su mensaje de 3 partes y la acción que lo resuelve. */
data class ErrorNotice(val message: UiMessage, val remedy: ErrorRemedy)
