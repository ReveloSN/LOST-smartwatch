package com.lost.domain.usecase

import com.lost.domain.metrics.MetricEventType
import com.lost.domain.model.Routine
import com.lost.domain.model.Trip
import com.lost.domain.port.RoutineLearningEngine
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import com.lost.domain.port.TripRecorder
import javax.inject.Inject

/**
 * Al terminar un recorrido: lo guarda, deja que la IA lo agrupe con recorridos parecidos
 * y guarda la rutina resultante. Avisa a las métricas cuando una rutina se confirma.
 */
class LearnRoutineFromTripUseCase @Inject constructor(
    private val tripRecorder: TripRecorder,
    private val routineReader: RoutineReader,
    private val routineWriter: RoutineWriter,
    private val learningEngine: RoutineLearningEngine,
    private val trackMetric: TrackMetricEventUseCase
) {
    suspend operator fun invoke(trip: Trip): Routine? {
        if (!trip.hasEnoughPoints) return null
        tripRecorder.record(trip)
        trackMetric(MetricEventType.TRIP_COMPLETED)
        val known = routineReader.routines()
        val learned = learningEngine.learn(trip, known) ?: return null
        routineWriter.save(learned)
        val wasConfirmedBefore = known.firstOrNull { it.id == learned.id }?.isConfirmed == true
        if (learned.isConfirmed && !wasConfirmedBefore) trackMetric(MetricEventType.ROUTINE_LEARNED)
        return learned
    }
}
