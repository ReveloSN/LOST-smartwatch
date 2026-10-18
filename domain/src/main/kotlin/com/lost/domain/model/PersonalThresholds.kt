package com.lost.domain.model

/**
 * Umbrales de probabilidad propios de cada usuario. Empiezan en valores por defecto
 * y se mueven con su retroalimentación ("Estoy bien" los sube; una ayuda confirmada los baja).
 */
data class PersonalThresholds(val medium: Double, val high: Double) {
    init {
        require(medium in 0.0..1.0 && high in 0.0..1.0) { "Los umbrales deben estar entre 0 y 1" }
        require(medium < high) { "El umbral medio debe ser menor que el alto" }
    }

    companion object {
        val DEFAULT = PersonalThresholds(medium = 0.45, high = 0.75)
    }
}
