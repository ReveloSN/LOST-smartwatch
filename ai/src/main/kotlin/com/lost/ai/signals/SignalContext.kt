package com.lost.ai.signals

import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Routine
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.Trip

/**
 * Lo que mide cada señal.
 * @property trip recorrido crudo (con tiempos reales, para paradas y retrasos).
 * @property path recorrido remuestreado cada 20 m (para forma y secuencia).
 * @property routinePath ruta de siempre remuestreada; vacía en modo lugar nuevo.
 * @property target destino al que se dirige (marcado o el de la rutina).
 */
data class SignalContext(
    val trip: Trip,
    val path: List<GeoPoint>,
    val routine: Routine?,
    val routinePath: List<GeoPoint>,
    val target: GeoPoint?,
    val progress: Double,
    val sensitivity: Sensitivity
) {
    val hasRoutine: Boolean get() = routine != null && routinePath.size >= 2
}
