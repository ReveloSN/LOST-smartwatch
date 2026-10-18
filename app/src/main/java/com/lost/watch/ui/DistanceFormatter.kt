package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import com.lost.watch.R
import java.text.NumberFormat
import kotlin.math.roundToInt

private const val METERS_PER_KILOMETER = 1000.0
private const val ROUNDING_METERS = 10

/** "180 m" o "1,2 km" según el idioma. Redondea a 10 m: más precisión no ayuda a caminar. */
@Composable
fun formatDistance(meters: Double): String {
    if (meters < METERS_PER_KILOMETER) {
        val rounded = (meters / ROUNDING_METERS).roundToInt() * ROUNDING_METERS
        return stringResource(R.string.distance_meters, rounded)
    }
    val format = NumberFormat.getNumberInstance(LocalConfiguration.current.locales[0]).apply {
        maximumFractionDigits = 1
    }
    return stringResource(R.string.distance_kilometers, format.format(meters / METERS_PER_KILOMETER))
}
