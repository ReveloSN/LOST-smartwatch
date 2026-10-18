package com.lost.watch.feature.learning

import com.lost.domain.model.PlaceKind

/** "Aprendiendo tu rutina": cuántas veces se ha visto y cuántas faltan para confirmarla. */
data class LearningUiState(val timesObserved: Int, val requiredRepetitions: Int, val placeKind: PlaceKind) {
    val remaining: Int get() = (requiredRepetitions - timesObserved).coerceAtLeast(0)
}
