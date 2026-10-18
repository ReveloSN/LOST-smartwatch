package com.lost.domain.model.units

/** Probabilidad entre 0 y 1. El constructor rechaza valores fuera de rango. */
@JvmInline
value class Probability(val value: Double) : Comparable<Probability> {
    init {
        require(value in 0.0..1.0) { "La probabilidad debe estar entre 0 y 1: $value" }
    }

    override fun compareTo(other: Probability): Int = value.compareTo(other.value)

    companion object {
        val ZERO = Probability(0.0)
        val CERTAIN = Probability(1.0)

        /** Recorta cualquier número al rango válido; útil al combinar señales. */
        fun clamped(value: Double) = Probability(value.coerceIn(0.0, 1.0))
    }
}
