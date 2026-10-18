package com.lost.domain.port

import com.lost.domain.assistance.HapticPattern

/** Hace vibrar el reloj con un patrón que comunica el estado. */
interface HapticNotifier {
    fun play(pattern: HapticPattern)
}
