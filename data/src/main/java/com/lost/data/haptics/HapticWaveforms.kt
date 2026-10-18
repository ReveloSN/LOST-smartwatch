package com.lost.data.haptics

import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.VibrationIntensity

/**
 * Traduce cada patrón de la lámina "Háptica" a una onda de vibración:
 * suave 1 × 40 ms · doble 2 × 80 ms con pausa de 120 · insistente 3 × 150 ms · giro largo + corto.
 */
object HapticWaveforms {
    private const val STRONG_AMPLITUDE = 255
    private const val SOFT_AMPLITUDE = 140
    private const val START = 0L
    private const val GENTLE_PULSE = 40L
    private const val DOUBLE_PULSE = 80L
    private const val DOUBLE_PAUSE = 120L
    private const val INSISTENT_PULSE = 150L
    private const val INSISTENT_PAUSE = 100L
    private const val DIRECTION_LONG = 300L
    private const val DIRECTION_PAUSE = 100L
    private const val DIRECTION_SHORT = 80L

    fun of(pattern: HapticPattern, intensity: VibrationIntensity): HapticWaveform {
        val amplitude = if (intensity == VibrationIntensity.STRONG) STRONG_AMPLITUDE else SOFT_AMPLITUDE
        val timings = timingsOf(pattern)
        val amplitudes = timings.indices.map { index -> if (index % 2 == 0) 0 else amplitude }
        return HapticWaveform(timings, amplitudes)
    }

    private fun timingsOf(pattern: HapticPattern): List<Long> = when (pattern) {
        HapticPattern.GENTLE -> listOf(START, GENTLE_PULSE)

        HapticPattern.DOUBLE -> listOf(START, DOUBLE_PULSE, DOUBLE_PAUSE, DOUBLE_PULSE)

        HapticPattern.INSISTENT ->
            listOf(START, INSISTENT_PULSE, INSISTENT_PAUSE, INSISTENT_PULSE, INSISTENT_PAUSE, INSISTENT_PULSE)

        HapticPattern.DIRECTION -> listOf(START, DIRECTION_LONG, DIRECTION_PAUSE, DIRECTION_SHORT)
    }
}
