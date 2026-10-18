package com.lost.data.haptics

import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.VibrationIntensity
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class HapticWaveformsTest {
    @Test
    fun `la doble vibra 2 veces 80 ms con 120 de pausa`() {
        val waveform = HapticWaveforms.of(HapticPattern.DOUBLE, VibrationIntensity.STRONG)
        assertEquals(listOf(0L, 80L, 120L, 80L), waveform.timings)
        assertEquals(listOf(0, 255, 0, 255), waveform.amplitudes)
    }

    @Test
    fun `la insistente vibra 3 veces`() {
        val waveform = HapticWaveforms.of(HapticPattern.INSISTENT, VibrationIntensity.STRONG)
        assertEquals(3, waveform.amplitudes.count { it > 0 })
    }

    @Test
    fun `la intensidad suave baja la amplitud`() {
        val soft = HapticWaveforms.of(HapticPattern.GENTLE, VibrationIntensity.SOFT)
        val strong = HapticWaveforms.of(HapticPattern.GENTLE, VibrationIntensity.STRONG)
        assertTrue(soft.amplitudes.max() < strong.amplitudes.max())
    }
}
