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
 * 05.3 · La pregunta como notificación del reloj: "LOST" en ámbar, la pregunta y dos botones. No vibra
 * por su cuenta (la escalada ya vibra con su patrón) y no se copia al teléfono.
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
            .addAction(0, context.getString(prompt.secondary.label), intentFor(prompt.secondary))
            .build()
        NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
    }

    override fun cancel() = NotificationManagerCompat.from(context).cancel(NOTIFICATION_ID)

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
        ).apply { enableVibration(false) }
        context.getSystemService(NotificationManager::class.java)?.createNotificationChannel(channel)
    }

    private companion object {
        const val CHANNEL_ID = "lost_questions"
        const val NOTIFICATION_ID = 2
    }
}
