package com.lost.domain.usecase

import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.RecordingMetricsTracker
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.AuthorizedContact
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.ContactNotifier
import com.lost.domain.port.ContactReader
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ShareLocationWithContactUseCaseTest {
    private val point = GeoPoint(4.6, -74.08)
    private val metrics = RecordingMetricsTracker()
    private val notifier = mockk<ContactNotifier>()

    private fun useCase(contact: AuthorizedContact?) = ShareLocationWithContactUseCase(
        ContactReader { flowOf(contact) },
        notifier,
        TrackMetricEventUseCase(metrics) { 1L }
    )

    @Test
    fun `sin contacto falla de forma explicita`() = runTest {
        val result = useCase(null)(point)
        assertEquals(LostResult.Failure(LostError.NoAuthorizedContact), result)
    }

    @Test
    fun `con contacto comparte y registra la metrica`() = runTest {
        coEvery { notifier.shareLocation(DomainFixtures.laura, point) } returns LostResult.Success(Unit)
        val result = useCase(DomainFixtures.laura)(point)
        assertEquals(LostResult.Success(DomainFixtures.laura), result)
        assertEquals(listOf(MetricEventType.LOCATION_SHARED), metrics.types)
    }

    @Test
    fun `sin conexion devuelve el error sin registrar metrica`() = runTest {
        coEvery { notifier.shareLocation(any(), any()) } returns LostResult.Failure(LostError.NoConnection)
        val result = useCase(DomainFixtures.laura)(point)
        assertEquals(LostResult.Failure(LostError.NoConnection), result)
        assertEquals(emptyList<MetricEventType>(), metrics.types)
    }
}
