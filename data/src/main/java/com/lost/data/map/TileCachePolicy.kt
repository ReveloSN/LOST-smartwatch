package com.lost.data.map

/** Límites de la caché de teselas (memoria y disco). */
data class TileCachePolicy(val memoryEntries: Int, val maxFiles: Int, val maxAgeMillis: Long) {
    companion object {
        private const val ONE_WEEK_MILLIS = 7L * 24 * 60 * 60 * 1000

        val DEFAULT = TileCachePolicy(memoryEntries = 48, maxFiles = 400, maxAgeMillis = ONE_WEEK_MILLIS)
    }
}
