package com.lost.watch.feature.demo

import com.lost.data.di.ApplicationScope
import com.lost.domain.usecase.DeleteLearnedDataUseCase
import com.lost.domain.usecase.UpdateSettingsUseCase
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * "Empezar de nuevo": detiene la demo, borra rutinas, recorridos y lugares, y vuelve a los ajustes de fábrica.
 * Con la bienvenida sin completar, la app vuelve a mostrarla, como a alguien que recién instala LOST. Sirve
 * para ensayar la presentación sin `adb shell pm clear`. Laura y los destinos de ejemplo se quedan.
 */
@Singleton
class DemoReset @Inject constructor(
    private val player: DemoPlayer,
    private val deleteLearnedData: DeleteLearnedDataUseCase,
    private val settings: UpdateSettingsUseCase,
    @param:ApplicationScope private val scope: CoroutineScope
) {
    fun startOver() {
        scope.launch {
            player.stop()
            deleteLearnedData()
            settings.startOver()
        }
    }
}
