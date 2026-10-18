package com.lost.domain.session

import com.lost.domain.port.ContactReader
import com.lost.domain.port.IdGenerator
import com.lost.domain.usecase.LearnRoutineFromTripUseCase
import javax.inject.Inject

/** Dependencias secundarias del monitor, agrupadas para no pasar muchos parámetros. */
class TripMonitorCollaborators @Inject constructor(
    val contactReader: ContactReader,
    val idGenerator: IdGenerator,
    val learnRoutine: LearnRoutineFromTripUseCase
)
