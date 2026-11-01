package com.lost.domain.usecase

import com.lost.domain.port.AlertPlaceRecorder
import com.lost.domain.port.RoutineWriter
import com.lost.domain.port.TripRecorder
import javax.inject.Inject

/** Privacidad → "Borrar lo aprendido": elimina rutinas, recorridos y los lugares donde LOST preguntó. */
class DeleteLearnedDataUseCase @Inject constructor(
    private val routineWriter: RoutineWriter,
    private val tripRecorder: TripRecorder,
    private val alertPlaceRecorder: AlertPlaceRecorder
) {
    suspend operator fun invoke() {
        routineWriter.deleteAll()
        tripRecorder.deleteAll()
        alertPlaceRecorder.deleteAll()
    }
}
