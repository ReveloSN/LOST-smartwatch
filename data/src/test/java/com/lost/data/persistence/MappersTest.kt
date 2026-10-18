package com.lost.data.persistence

import com.lost.data.persistence.entity.MetricEventEntity
import com.lost.data.persistence.mapper.DestinationEntityMapper
import com.lost.data.persistence.mapper.MetricEventEntityMapper
import com.lost.data.persistence.mapper.RoutineEntityMapper
import com.lost.data.persistence.mapper.TripEntityMapper
import com.lost.domain.metrics.MetricEvent
import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.Destination
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.PlaceKind
import com.lost.domain.model.Trip
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class MappersTest {
    @Test
    fun `una rutina va y vuelve de la base de datos igual`() {
        val routine = DataFixtures.routine()
        val mapper = RoutineEntityMapper()
        assertEquals(routine, mapper.toDomain(mapper.toEntity(routine)))
    }

    @Test
    fun `un tipo de lugar desconocido se lee como OTHER`() {
        val mapper = RoutineEntityMapper()
        val entity = mapper.toEntity(DataFixtures.routine()).copy(destinationKind = "SPACESHIP")
        assertEquals(PlaceKind.OTHER, mapper.toDomain(entity).destinationKind)
    }

    @Test
    fun `un recorrido va y vuelve igual`() {
        val trip = Trip("t1", listOf(GeoPoint(4.6, -74.0, 10L), GeoPoint(4.7, -74.1, 20L)))
        val mapper = TripEntityMapper()
        val entity = mapper.toEntity(trip)
        assertEquals(10L, entity.startedAtMillis)
        assertEquals(trip, mapper.toDomain(entity))
    }

    @Test
    fun `un destino va y vuelve igual`() {
        val destination = Destination("d1", "Biblioteca", GeoPoint(4.6, -74.0), PlaceKind.LIBRARY, 5L)
        val mapper = DestinationEntityMapper()
        assertEquals(destination, mapper.toDomain(mapper.toEntity(destination)))
    }

    @Test
    fun `un evento de metricas desconocido se descarta`() {
        val mapper = MetricEventEntityMapper()
        val event = MetricEvent(MetricEventType.ALERT_SHOWN, 1L, 2L)
        assertEquals(event, mapper.toDomain(mapper.toEntity(event)))
        assertNull(mapper.toDomain(MetricEventEntity(type = "OLD_EVENT", timestampMillis = 1L, elapsedMillis = null)))
    }
}
