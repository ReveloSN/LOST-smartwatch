package com.lost.data.persistence

import app.cash.turbine.test
import com.lost.data.persistence.mapper.RoutineEntityMapper
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class RoomRoutineRepositoryTest {
    private val repository = RoomRoutineRepository(FakeRoutineDao(), RoutineEntityMapper())

    @Test
    fun `guardar y leer una rutina`() = runTest {
        repository.save(DataFixtures.routine())
        assertEquals(DataFixtures.routine(), repository.routine("r1"))
    }

    @Test
    fun `observar emite cada cambio`() = runTest {
        repository.observeRoutines().test {
            assertEquals(0, awaitItem().size)
            repository.save(DataFixtures.routine("a"))
            assertEquals(1, awaitItem().size)
            repository.save(DataFixtures.routine("b"))
            assertEquals(2, awaitItem().size)
            repository.deleteAll()
            assertEquals(0, awaitItem().size)
        }
    }

    @Test
    fun `borrar elimina solo esa rutina`() = runTest {
        repository.save(DataFixtures.routine("a"))
        repository.save(DataFixtures.routine("b"))
        repository.delete("a")
        assertNull(repository.routine("a"))
        assertEquals(listOf("b"), repository.routines().map { it.id })
    }
}
