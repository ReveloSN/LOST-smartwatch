package com.lost.watch.feature.routines

/** Estado de "Mis recorridos". */
data class RoutinesUiState(val isLoading: Boolean, val items: List<RoutineItem>) {
    val isEmpty: Boolean get() = !isLoading && items.isEmpty()
}
