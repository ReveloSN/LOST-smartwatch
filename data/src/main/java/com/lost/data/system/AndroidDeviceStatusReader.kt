package com.lost.data.system

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.BatteryManager
import com.lost.domain.port.DeviceStatusReader
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/** Lee la batería y el permiso de ubicación del reloj. */
class AndroidDeviceStatusReader @Inject constructor(@ApplicationContext private val context: Context) :
    DeviceStatusReader {
    override fun batteryPercent(): Int? = context.getSystemService(BatteryManager::class.java)
        ?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        ?.takeIf { it in 0..FULL_BATTERY }

    override fun hasLocationPermission(): Boolean =
        context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private companion object {
        const val FULL_BATTERY = 100
    }
}
