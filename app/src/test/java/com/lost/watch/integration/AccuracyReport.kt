package com.lost.watch.integration

/** Qué precisión informa el GPS simulado en cada punto (`Location.accuracy`). */
enum class AccuracyReport {
    /** No informa precisión, como los GPX de la demo. */
    NONE,

    /** Informa el radio del 68 % de su error; un salto llega con la precisión mala que de verdad tiene. */
    HONEST,

    /** Peor caso: los saltos llegan con la misma precisión que los puntos buenos. */
    HIDES_SPIKES
}
