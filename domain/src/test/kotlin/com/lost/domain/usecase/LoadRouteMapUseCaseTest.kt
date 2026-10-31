package com.lost.domain.usecase

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.map.MapTile
import com.lost.domain.map.ViewportFrame
import com.lost.domain.model.GeoPoint
import java.util.concurrent.atomic.AtomicInteger
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class LoadRouteMapUseCaseTest {
    private val route = listOf(GeoPoint(4.6300, -74.0760), GeoPoint(4.6387, -74.0852))
    private val frame = ViewportFrame(300, 300, 0.2)

    @Test
    fun `trae solo las teselas visibles`() = runTest {
        val requests = AtomicInteger()
        val useCase =
            LoadRouteMapUseCase { key ->
                requests.incrementAndGet()
                LostResult.Success(MapTile(key, ByteArray(1)))
            }
        val map = useCase(route, frame).valueOrNull()!!
        assertEquals(map.viewport.visibleTiles().size, requests.get())
        assertEquals(requests.get(), map.tiles.size)
    }

    @Test
    fun `sin conexion devuelve mapa no disponible`() = runTest {
        val useCase = LoadRouteMapUseCase { LostResult.Failure(LostError.MapUnavailable) }
        assertEquals(LostError.MapUnavailable, useCase(route, frame).errorOrNull())
    }
}
