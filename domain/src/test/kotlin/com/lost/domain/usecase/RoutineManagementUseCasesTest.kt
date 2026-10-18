package com.lost.domain.usecase

import app.cash.turbine.test
import com.lost.domain.fakes.DomainFixtures
import com.lost.domain.fakes.FakeRoutineRepository
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RoutineManagementUseCasesTest {
    private val repository = FakeRoutineRepository(
        listOf(
            DomainFixtures.routine(id = "learning", isConfirmed = false, departure = 300),
            DomainFixtures.routine(id = "late", isConfirmed = true, departure = 1000),
            DomainFixtures.routine(id = "early", isConfirmed = true, departure = 400)
        )
    )

    @Test
    fun `mis recorridos muestra primero las confirmadas por hora de salida`() = runTest {
        ObserveRoutinesUseCase(repository)().test {
            assertEquals(listOf("early", "late", "learning"), awaitItem().map { it.id })
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `confirmar marca la rutina como guardada`() = runTest {
        ConfirmRoutineUseCase(repository, repository)("learning")
        assertTrue(repository.routine("learning")!!.isConfirmed)
    }

    @Test
    fun `renombrar ignora nombres vacios`() = runTest {
        val rename = RenameRoutineUseCase(repository, repository)
        rename("early", "   ")
        assertEquals("Casa → U", repository.routine("early")!!.name)
        rename("early", " Gimnasio ")
        assertEquals("Gimnasio", repository.routine("early")!!.name)
    }

    @Test
    fun `borrar elimina la rutina`() = runTest {
        DeleteRoutineUseCase(repository)("late")
        assertNull(repository.routine("late"))
    }
}
