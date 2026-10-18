package com.lost.ai.learning

/** Una rutina se confirma cuando el recorrido se repite las veces necesarias (5 por defecto). */
class RoutineConfirmationPolicy(private val requiredRepetitions: Int) {
    fun isConfirmed(timesObserved: Int): Boolean = timesObserved >= requiredRepetitions
}
