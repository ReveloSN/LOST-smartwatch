package com.lost.domain.model

/**
 * Sensibilidad de la detección. [toleranceFactor] multiplica la duración habitual:
 * con una ruta de 35 min, Baja pregunta a los 65, Media a los 50 y Alta a los 42 (lámina 04.2).
 */
enum class Sensitivity(val toleranceFactor: Double) {
    LOW(toleranceFactor = 1.86),
    MEDIUM(toleranceFactor = 1.43),
    HIGH(toleranceFactor = 1.2)
}
