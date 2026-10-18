package com.lost.data.location

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.lifecycle.LifecycleService
import com.lost.data.R

/**
 * Servicio en primer plano de tipo ubicación: mantiene el GPS activo con la pantalla apagada
 * y muestra una notificación tranquila ("LOST te acompaña"). La lógica corre en la app.
 */
class LocationTrackingService : LifecycleService() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        createChannel()
        ServiceCompat.startForeground(
            this,
            NOTIFICATION_ID,
            notification(),
            ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
        )
        return START_STICKY
    }

    private fun notification(): Notification = NotificationCompat.Builder(this, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_lost_notification)
        .setContentTitle(getString(R.string.tracking_notification_title))
        .setContentText(getString(R.string.tracking_notification_text))
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_NAVIGATION)
        .build()

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.tracking_notification_channel),
            NotificationManager.IMPORTANCE_LOW
        )
        getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    companion object {
        private const val CHANNEL_ID = "lost_tracking"
        private const val NOTIFICATION_ID = 1

        fun start(context: Context) {
            context.startForegroundService(Intent(context, LocationTrackingService::class.java))
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, LocationTrackingService::class.java))
        }
    }
}
