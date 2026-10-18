package com.lost.watch.feature.learning

/** Acciones en "Aprendiendo tu rutina". */
sealed interface LearningUiEvent {
    /** Confirmarla ya, sin esperar las repeticiones que faltan. */
    data object ConfirmNow : LearningUiEvent
}
