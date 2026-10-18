package com.lost.watch.feature.onboarding

/** Contacto sugerido para la bienvenida (en la demo, Laura). */
data class OnboardingUiState(val suggestedContactName: String?, val suggestedContactRelationship: String?) {
    val hasSuggestedContact: Boolean get() = suggestedContactName != null
}
