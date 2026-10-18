package com.lost.ai.signals

/**
 * Una señal mide una sola cosa (responsabilidad única). Para agregar una señal nueva
 * basta con implementar esta interfaz y registrarla: el puntaje combinado no cambia (abierto/cerrado).
 */
interface DeviationSignal {
    val kind: SignalKind

    fun measure(context: SignalContext): SignalReading
}
