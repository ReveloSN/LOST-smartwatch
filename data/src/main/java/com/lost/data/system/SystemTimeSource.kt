package com.lost.data.system

import com.lost.domain.port.TimeSource
import javax.inject.Inject

/** Hora real del reloj. */
class SystemTimeSource @Inject constructor() : TimeSource {
    override fun nowMillis(): Long = System.currentTimeMillis()
}
