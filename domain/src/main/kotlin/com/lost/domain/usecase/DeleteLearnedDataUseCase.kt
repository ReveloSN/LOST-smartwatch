package com.lost.domain.usecase

import com.lost.domain.port.RoutineWriter
import com.lost.domain.port.TripRecorder
import javax.inject.Inject

/** Privacidad → "Borrar lo aprendido": elimina rutinas y recorridos guardados. */
class DeleteLearnedDataUseCase @Inject constructor(
    private val routineWriter: RoutineWriter,
    private val tripRecorder: TripRecorder
) {
    suspend operator fun invoke() {
        routineWriter.deleteAll()
        tripRecorder.deleteAll()
    }
}
