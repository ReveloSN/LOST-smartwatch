package com.lost.watch.feature.routines

/** Acciones sobre una rutina (detalle). */
sealed interface RoutinesUiEvent {
    data object Confirm : RoutinesUiEvent

    data class Rename(val newName: String) : RoutinesUiEvent

    /** Primer toque arma el borrado, el segundo lo confirma. */
    data object DeleteTapped : RoutinesUiEvent
}
