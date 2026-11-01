package com.lost.watch.glance

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.lost.watch.MainActivity

/** Tocar la complicación o la vista rápida abre Inicio. */
fun openLostIntent(context: Context): PendingIntent = PendingIntent.getActivity(
    context,
    0,
    Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK),
    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
)
