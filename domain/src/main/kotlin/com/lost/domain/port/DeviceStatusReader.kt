package com.lost.domain.port

/** Estado del reloj que afecta al acompañamiento: batería y permiso de ubicación. */
interface DeviceStatusReader {
    /** Porcentaje de batería, o `null` si no se puede leer. */
    fun batteryPercent(): Int?

    fun hasLocationPermission(): Boolean
}
