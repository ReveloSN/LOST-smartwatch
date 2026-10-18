package com.lost.domain.assistance

/** Tiempos de la escalada de ayuda (lámina "Respuesta escalonada"). */
object AssistanceTimings {
    /** Sin respuesta durante este tiempo → se comparte la ubicación. */
    const val NO_RESPONSE_TIMEOUT_SECONDS = 30

    /** Se repite la vibración doble si no hay respuesta. */
    const val REPEAT_ASK_SECONDS = 15

    /** Cuenta regresiva cancelable antes de avisar al contacto. */
    const val SHARING_COUNTDOWN_SECONDS = 5

    /** "Entendido" se queda en pantalla este tiempo antes de volver a Inicio. */
    const val ACKNOWLEDGEMENT_SECONDS = 3
}
