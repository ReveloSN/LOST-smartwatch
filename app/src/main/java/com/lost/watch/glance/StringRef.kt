package com.lost.watch.glance

import android.content.Context
import androidx.annotation.StringRes

/** Un texto de strings.xml con su argumento (si lo tiene), para decidirlo sin Android y resolverlo al dibujar. */
data class StringRef(@param:StringRes val id: Int, val argument: String? = null) {
    fun resolve(context: Context): String = argument?.let { context.getString(id, it) } ?: context.getString(id)
}
