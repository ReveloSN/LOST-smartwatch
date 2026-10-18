package com.lost.data.haptics

/** Tiempos (ms, alternando pausa y vibración) y amplitudes (0–255) de un patrón. */
data class HapticWaveform(val timings: List<Long>, val amplitudes: List<Int>)
