package com.lost.watch.feature.onboarding

/** Acciones del usuario durante la bienvenida. */
sealed interface OnboardingUiEvent {
    /** Elige al contacto sugerido o sigue sin contacto; en ambos casos termina la bienvenida. */
    data object Finish : OnboardingUiEvent
}
