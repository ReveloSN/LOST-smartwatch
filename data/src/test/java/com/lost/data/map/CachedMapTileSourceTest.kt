package com.lost.data.map

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.map.LruCache
import com.lost.domain.map.MapTile
import com.lost.domain.map.TileKey
import com.lost.domain.port.MapTileSource
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Test

class CachedMapTileSourceTest {
    private val key = TileKey(16, 19281, 31922)
    private val png = byteArrayOf(1, 2, 3)
    private val policy = TileCachePolicy(memoryEntries = 4, maxFiles = 2, maxAgeMillis = 1_000)
    private var now = System.currentTimeMillis()
    private val directory: File = Files.createTempDirectory("tiles").toFile()
    private var downloads = 0
    private val network = MapTileSource { tileKey ->
        downloads++
        LostResult.Success(MapTile(tileKey, png))
    }

    private fun source(net: MapTileSource = network) = CachedMapTileSource(
        LruCache(policy.memoryEntries),
        TileDiskCache(directory, policy) {
            now
        },
        net,
        Dispatchers.Unconfined
    )

    @Test
    fun `la misma tesela se descarga una sola vez`() = runTest {
        val tiles = source()
        tiles.tile(key)
        tiles.tile(key)
        assertEquals(1, downloads)
    }

    @Test
    fun `tras reiniciar la app la tesela sale del disco`() = runTest {
        source().tile(key)
        val offline = source { LostResult.Failure(LostError.MapUnavailable) }
        assertArrayEquals(png, offline.tile(key).valueOrNull()?.png)
    }

    @Test
    fun `el disco guarda como mucho maxFiles teselas`() = runTest {
        val tiles = source()
        (0 until 5).forEach { tiles.tile(TileKey(16, it, 0)) }
        assertEquals(policy.maxFiles, directory.listFiles()!!.size)
    }

    @Test
    fun `una tesela vencida no se usa`() {
        val disk = TileDiskCache(directory, policy) { now }
        disk.write(key, png)
        now += policy.maxAgeMillis * 10_000
        assertFalse(disk.read(key) != null)
    }
}
