package com.lost.watch.integration

import com.lost.domain.model.Destination

/** Empieza y termina el acompañamiento de un recorrido. La UI no sabe si hay un servicio detrás. */
interface MonitoringController {
    fun start(destination: Destination? = null)

    fun stop()
}
