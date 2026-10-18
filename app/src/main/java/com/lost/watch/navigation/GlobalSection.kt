package com.lost.watch.navigation

import androidx.annotation.StringRes
import com.lost.watch.R

/** Las cinco secciones globales (sistema de organización), en el orden en que se recorren. */
enum class GlobalSection(@param:StringRes val label: Int) {
    HOME(R.string.nav_home),
    ROUTINES(R.string.nav_routines),
    DESTINATIONS(R.string.nav_destinations),
    CONTACTS(R.string.nav_contacts),
    SETTINGS(R.string.nav_settings)
}
