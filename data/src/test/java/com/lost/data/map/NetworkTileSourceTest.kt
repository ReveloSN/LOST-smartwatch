package com.lost.data.map

import com.lost.data.online.HttpGetClient
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.map.TileKey
import java.io.IOException
import java.util.concurrent.CountDownLatch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class NetworkTileSourceTest {
    private val key = TileKey(12, 1168, 2033)
    private val unavailable = LostResult.Failure(LostError.MapUnavailable)

    @Test
    fun `una descarga colgada se abandona al pasar el tope`() = runTest {
        val release = CountDownLatch(1)
        val http = HttpGetClient {
            release.await()
            byteArrayOf()
        }
        try {
            assertEquals(unavailable, NetworkTileSource(http, Dispatchers.IO).tile(key))
        } finally {
            release.countDown()
        }
    }

    @Test
    fun `un error de red deja el mapa no disponible`() = runTest {
        val http = HttpGetClient { throw IOException("sin red") }
        assertEquals(unavailable, NetworkTileSource(http, StandardTestDispatcher(testScheduler)).tile(key))
    }

    @Test
    fun `una descarga correcta devuelve la tesela`() = runTest {
        val png = byteArrayOf(1, 2, 3)
        val source = NetworkTileSource({ png }, StandardTestDispatcher(testScheduler))
        assertArrayEquals(png, source.tile(key).valueOrNull()?.png)
    }
}
