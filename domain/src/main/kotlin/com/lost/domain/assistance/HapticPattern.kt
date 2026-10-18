package com.lost.domain.assistance

/**
 * Patrones de vibración de la lámina "Háptica". El dominio solo dice cuál;
 * `data` sabe cómo se reproduce cada uno en el reloj.
 */
enum class HapticPattern {
    /** 1 toque de 40 ms: confirmación, recorrido reconocido. */
    GENTLE,

    /** 2 × 80 ms con pausa de 120 ms: nivel medio, "¿Necesitas ayuda?". */
    DOUBLE,

    /** 3 × 150 ms: nivel alto y cuenta regresiva de aviso. */
    INSISTENT,

    /** Largo + corto: avisa un giro durante la guía. */
    DIRECTION
}
