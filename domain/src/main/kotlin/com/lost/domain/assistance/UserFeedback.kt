package com.lost.domain.assistance

/** Respuestas del usuario que la IA usa para aprender. */
enum class UserFeedback {
    /** "Estoy bien": la alerta sobraba → se sube el umbral personal. */
    IM_FINE,

    /** "Voy a otro lado": desvío voluntario → se aprende el desvío. */
    GOING_ELSEWHERE,

    /** "Sí, guíame": la ayuda era necesaria → se baja el umbral personal. */
    HELP_CONFIRMED
}
