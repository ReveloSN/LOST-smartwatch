package com.lost.data.haptics

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.lost.data.di.ApplicationScope
import com.lost.domain.assistance.HapticPattern
import com.lost.domain.model.VibrationIntensity
import com.lost.domain.port.HapticNotifier
import com.lost.domain.port.SettingsReader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Reproduce los patrones hápticos con el vibrador del reloj, con la intensidad elegida en Ajustes. */
@Singleton
class AndroidHapticNotifier @Inject constructor(
    @ApplicationContext private val context: Context,
    settingsReader: SettingsReader,
    @ApplicationScope scope: CoroutineScope
) : HapticNotifier {
    private val intensity = settingsReader.observeSettings()
        .map { it.vibrationIntensity }
        .stateIn(scope, SharingStarted.Eagerly, VibrationIntensity.STRONG)

    override fun play(pattern: HapticPattern) {
        val vibrator = vibrator() ?: return
        if (!vibrator.hasVibrator()) return
        val waveform = HapticWaveforms.of(pattern, intensity.value)
        val effect = if (vibrator.hasAmplitudeControl()) {
            VibrationEffect.createWaveform(waveform.timings.toLongArray(), waveform.amplitudes.toIntArray(), NO_REPEAT)
        } else {
            VibrationEffect.createWaveform(waveform.timings.toLongArray(), NO_REPEAT)
        }
        vibrator.vibrate(effect)
    }

    private fun vibrator(): Vibrator? = context.getSystemService(VibratorManager::class.java)?.defaultVibrator
        ?: context.getSystemService(Vibrator::class.java)

    private companion object {
        const val NO_REPEAT = -1
    }
}
