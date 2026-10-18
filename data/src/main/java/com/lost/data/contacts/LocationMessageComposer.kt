package com.lost.data.contacts

import android.content.Context
import com.lost.data.R
import com.lost.domain.model.GeoPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject

/** Arma el mensaje calmado que recibe el contacto, con un enlace al mapa. */
class LocationMessageComposer @Inject constructor(@ApplicationContext private val context: Context) {
    fun compose(location: GeoPoint): String {
        val link = String.format(Locale.ROOT, MAP_LINK, location.latitude, location.longitude)
        return context.getString(R.string.contact_location_message, link)
    }

    private companion object {
        const val MAP_LINK = "https://maps.google.com/?q=%.6f,%.6f"
    }
}
