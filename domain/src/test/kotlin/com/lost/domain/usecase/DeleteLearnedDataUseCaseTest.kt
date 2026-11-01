package com.lost.domain.usecase

import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeRoutineRepository
import com.lost.domain.model.AlertPlace
import com.lost.domain.model.Trip
import com.lost.domain.port.AlertPlaceRecorder
import com.lost.domain.port.TripRecorder
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/** Privacidad: "Borrar lo aprendido" no deja nada de lo que LOST guardó sobre tus recorridos. */
class DeleteLearnedDataUseCaseTest {
    @Test
    fun `borra rutinas recorridos y los lugares donde lost pregunto`() = runTest {
        val routines = FakeRoutineRepository(listOf(DomainFixtures.routine()))
        val trips = mutableListOf(DomainFixtures.trip())
        val places = mutableListOf(AlertPlace(DomainFixtures.trip().points.first(), 1L, null, null))
        val tripRecorder = object : TripRecorder {
            override suspend fun record(trip: Trip) {
                trips += trip
            }

            override suspend fun deleteAll() = trips.clear()
        }
        val placeRecorder = object : AlertPlaceRecorder {
            override suspend fun record(place: AlertPlace) {
                places += place
            }

            override suspend fun deleteAll() = places.clear()
        }

        DeleteLearnedDataUseCase(routines, tripRecorder, placeRecorder)()

        assertEquals(emptyList<Any>(), routines.routines())
        assertTrue(trips.isEmpty())
        assertTrue(places.isEmpty())
    }
}
