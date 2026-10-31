package com.lost.data.map

import com.lost.domain.map.TileKey
import java.io.File

/**
 * Caché en disco de teselas: un archivo por tesela, vigente una semana y con tope de archivos
 * (al pasarse se borran los más viejos). Así una misma zona no se vuelve a descargar.
 */
class TileDiskCache(private val directory: File, private val policy: TileCachePolicy, private val now: () -> Long) {
    fun read(key: TileKey): ByteArray? {
        val file = fileOf(key)
        if (!file.exists() || now() - file.lastModified() > policy.maxAgeMillis) return null
        return file.readBytes()
    }

    fun write(key: TileKey, png: ByteArray) {
        directory.mkdirs()
        fileOf(key).writeBytes(png)
        evictOldest()
    }

    private fun evictOldest() {
        val files = directory.listFiles()?.sortedBy { it.lastModified() } ?: return
        files.take((files.size - policy.maxFiles).coerceAtLeast(0)).forEach { it.delete() }
    }

    private fun fileOf(key: TileKey) = File(directory, "${key.zoom}_${key.x}_${key.y}.png")
}
