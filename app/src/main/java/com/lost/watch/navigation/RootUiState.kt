package com.lost.watch.navigation

/** Lo que la raíz necesita saber antes de dibujar: si terminó la bienvenida y si usa texto grande. */
data class RootUiState(val isLoading: Boolean, val isOnboardingCompleted: Boolean, val isLargeTextEnabled: Boolean) {
    companion object {
        val LOADING = RootUiState(isLoading = true, isOnboardingCompleted = false, isLargeTextEnabled = false)
    }
}
