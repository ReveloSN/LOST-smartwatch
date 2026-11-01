package com.lost.watch.integration

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.ui.graphics.toArgb
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.lost.data.R as DataR
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.MainActivity
import com.lost.watch.R
import com.lost.watch.glance.openLostIntent
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * 05.3 · La pregunta como notificación del reloj: "LOST" en ámbar, la pregunta y sus botones. No suena (LOST es
 * discreto) y no se copia al teléfono. Vibra un solo toque de 40 ms: Wear OS deja una notificación sin sonido ni
 * vibración en la sección silenciosa, sin anunciarla, y con la pantalla apagada la pregunta pasaría inadvertida.
 * El patrón propio de cada nivel lo sigue dando la escalada.
 */
class AndroidPromptNotifications @Inject constructor(@ApplicationContext private val context: Context) :
    PromptNotifications {
    override fun show(prompt: AssistancePrompt) {
        if (!canNotify()) return
        createChannel()
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(DataR.drawable.ic_lost_notification)
            .setContentTitle(context.getString(R.string.tile_label))
            .setContentText(context.getString(prompt.question))
            .setColor(SemanticTokens.Status.deviation.toArgb())
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setLocalOnly(true)
            .setAutoCancel(true)
            .setContentIntent(openLostIntent(context))
            .addAction(0, context.getString(prompt.primary.label), intentFor(prompt.primary))
            .apply { prompt.secondary?.let { addAction(0, context.getString(it.label), intentFor(it)) } }
            .build()
        try {
            cancel()
            NotificationManagerCompat.from(context).notify(idOf(prompt), notification)
        } catch (_: SecurityException) {
            // El permiso se revocó entre la comprobación y el aviso: la alerta en pantalla sigue funcionando.
        }
    }

    override fun cancel() {
        val manager = NotificationManagerCompat.from(context)
        AssistancePrompt.entries.forEach { manager.cancel(idOf(it)) }
    }

    /**
     * Cada pregunta tiene su propio id: una notificación que solo se actualiza no vuelve a aparecer en pantalla,
     * y la cuenta regresiva (5 s para cancelar) tiene que verse aunque reemplace a "¿Todo bien en tu ruta?".
     */
    private fun idOf(prompt: AssistancePrompt) = FIRST_NOTIFICATION_ID + prompt.ordinal

    /** "Guíame" abre la app directamente: Android no deja abrir una pantalla desde un receptor de notificación. */
    private fun intentFor(action: PromptAction): PendingIntent {
        val flags = PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        return if (action.opensApp) {
            val intent = Intent(context, MainActivity::class.java)
                .putExtra(PromptAction.EXTRA, action.name)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            PendingIntent.getActivity(context, action.ordinal + 1, intent, flags)
        } else {
            val intent = Intent(context, PromptActionReceiver::class.java).putExtra(PromptAction.EXTRA, action.name)
            PendingIntent.getBroadcast(context, action.ordinal + 1, intent, flags)
        }
    }

    private fun canNotify(): Boolean = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
        PackageManager.PERMISSION_GRANTED

    private fun createChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_questions),
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            enableVibration(true)
            vibrationPattern = ANNOUNCE_VIBRATION
            setSound(null, null)
        }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "lost_prompts_alert"
        const val FIRST_NOTIFICATION_ID = 2
        val ANNOUNCE_VIBRATION = longArrayOf(0, 40)
    }
}
