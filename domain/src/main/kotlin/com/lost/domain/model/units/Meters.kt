package com.lost.domain.model.units

/** Distancia en metros. Tipo propio para no mezclar metros con grados o segundos. */
@JvmInline
value class Meters(val value: Double) : Comparable<Meters> {
    operator fun plus(other: Meters) = Meters(value + other.value)

    operator fun minus(other: Meters) = Meters(value - other.value)

    operator fun times(factor: Double) = Meters(value * factor)

    override fun compareTo(other: Meters): Int = value.compareTo(other.value)

    companion object {
        val ZERO = Meters(0.0)
    }
}
