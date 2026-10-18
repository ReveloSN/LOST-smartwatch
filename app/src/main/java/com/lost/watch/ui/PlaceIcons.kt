package com.lost.watch.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flight
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.LocalCafe
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.LocalLibrary
import androidx.compose.material.icons.rounded.Park
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.Storefront
import androidx.compose.material.icons.rounded.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.lost.domain.model.PlaceKind

/** Icono reconocible para cada tipo de lugar (lámina "Átomos · Iconos"). */
object PlaceIcons {
    fun of(kind: PlaceKind): ImageVector = when (kind) {
        PlaceKind.HOME -> Icons.Rounded.Home
        PlaceKind.UNIVERSITY -> Icons.Rounded.School
        PlaceKind.WORK -> Icons.Rounded.Work
        PlaceKind.GYM -> Icons.Rounded.FitnessCenter
        PlaceKind.CAFE -> Icons.Rounded.LocalCafe
        PlaceKind.PARK -> Icons.Rounded.Park
        PlaceKind.HOSPITAL -> Icons.Rounded.LocalHospital
        PlaceKind.AIRPORT -> Icons.Rounded.Flight
        PlaceKind.LIBRARY -> Icons.Rounded.LocalLibrary
        PlaceKind.RESTAURANT -> Icons.Rounded.Restaurant
        PlaceKind.STORE -> Icons.Rounded.Storefront
        PlaceKind.OTHER -> Icons.Rounded.Place
    }
}
