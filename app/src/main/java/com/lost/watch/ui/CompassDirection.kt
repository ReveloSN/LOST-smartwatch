package com.lost.watch.ui

import androidx.annotation.StringRes
import com.lost.watch.R
import kotlin.math.roundToInt

/** Los 8 rumbos en palabras, para que TalkBack diga hacia dónde apunta la flecha. */
enum class CompassDirection(@param:StringRes val label: Int) {
    NORTH(R.string.direction_north),
    NORTH_EAST(R.string.direction_north_east),
    EAST(R.string.direction_east),
    SOUTH_EAST(R.string.direction_south_east),
    SOUTH(R.string.direction_south),
    SOUTH_WEST(R.string.direction_south_west),
    WEST(R.string.direction_west),
    NORTH_WEST(R.string.direction_north_west);

    companion object {
        private const val SECTOR_DEGREES = 45f
        private const val FULL_TURN = 360f

        fun of(bearingDegrees: Float): CompassDirection {
            val normalized = ((bearingDegrees % FULL_TURN) + FULL_TURN) % FULL_TURN
            return entries[(normalized / SECTOR_DEGREES).roundToInt() % entries.size]
        }
    }
}
