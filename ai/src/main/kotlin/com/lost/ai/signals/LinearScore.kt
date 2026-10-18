package com.lost.ai.signals

/** Convierte un valor en un puntaje 0..1 que sube linealmente entre `start` y `end`. */
internal object LinearScore {
    fun between(value: Double, start: Double, end: Double): Double {
        if (end <= start) return if (value >= end) 1.0 else 0.0
        return ((value - start) / (end - start)).coerceIn(0.0, 1.0)
    }
}
