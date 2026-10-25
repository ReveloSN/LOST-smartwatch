package com.lost.data.sensors

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.lost.domain.port.CompassHeadingSource
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlin.math.abs
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.emptyFlow

/**
 * Brújula del reloj con el sensor de rotación (Adapter → [CompassHeadingSource]).
 * Solo está registrada mientras alguien escucha y descarta cambios menores de 3°
 * para no redibujar la flecha por temblores de la muñeca. Sin sensor, no emite (flecha absoluta).
 */
class AndroidCompassHeadingSource @Inject constructor(@param:ApplicationContext private val context: Context) :
    CompassHeadingSource {
    override fun headings(): Flow<Double> {
        val manager = context.getSystemService(SensorManager::class.java) ?: return emptyFlow()
        val sensor = manager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) ?: return emptyFlow()
        return callbackFlow {
            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    trySend(azimuthOf(event.values))
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
            }
            manager.registerListener(listener, sensor, SensorManager.SENSOR_DELAY_UI)
            awaitClose { manager.unregisterListener(listener) }
        }.distinctUntilChanged { old, new -> abs(old - new) < MIN_CHANGE_DEGREES }
    }

    private fun azimuthOf(rotationVector: FloatArray): Double {
        val rotation = FloatArray(MATRIX_SIZE)
        val orientation = FloatArray(AXES)
        SensorManager.getRotationMatrixFromVector(rotation, rotationVector)
        SensorManager.getOrientation(rotation, orientation)
        return (Math.toDegrees(orientation[0].toDouble()) + FULL_TURN) % FULL_TURN
    }

    private companion object {
        const val MATRIX_SIZE = 9
        const val AXES = 3
        const val FULL_TURN = 360.0
        const val MIN_CHANGE_DEGREES = 3.0
    }
}
