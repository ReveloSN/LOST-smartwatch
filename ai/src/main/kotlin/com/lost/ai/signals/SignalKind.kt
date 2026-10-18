package com.lost.ai.signals

/** Tipos de señal del motor y su grupo dentro de la fórmula de puntaje. */
enum class SignalKind(val group: SignalGroup) {
    OFF_ROUTE(SignalGroup.DEVIATION),
    SEQUENCE_DIVERGENCE(SignalGroup.DEVIATION),
    DELAY(SignalGroup.DELAY),
    LOOP(SignalGroup.CONFUSION),
    ERRATIC_HEADING(SignalGroup.CONFUSION),
    MOVING_AWAY(SignalGroup.CONFUSION),
    UNUSUAL_STOP(SignalGroup.CONFUSION)
}
