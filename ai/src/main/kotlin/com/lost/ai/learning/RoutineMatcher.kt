package com.lost.ai.learning

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.HaversineDistanceCalculator
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.ai.sequence.SequenceComparator
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Routine
import java.util.PriorityQueue

/**
 * ¿A qué rutina pertenece este recorrido? Filtra por punto de partida, compara secuencias
 * y ordena con una `PriorityQueue` (la de menor costo sale primero).
 */
class RoutineMatcher(
    private val comparator: SequenceComparator,
    private val resampler: TrajectoryResampler,
    private val thresholds: DetectionThresholds,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) {
    /** Recorrido en curso: la rutina más parecida que sale del mismo lugar, aunque hoy se haya desviado. */
    fun bestOngoingMatch(path: List<GeoPoint>, routines: List<Routine>): RoutineMatch? =
        rank(path, routines.filter { startsNear(it, path) }, isPartial = true).poll()

    /** Recorrido terminado: solo si sale y llega a los mismos lugares y la secuencia se parece. */
    fun bestCompletedMatch(path: List<GeoPoint>, routines: List<Routine>): RoutineMatch? {
        val candidates = routines.filter { startsNear(it, path) && endsNear(it, path) }
        return rank(path, candidates, isPartial = false).poll()
            ?.takeIf { it.cost <= thresholds.routineMatchMaxCostMeters }
    }

    /** Las `k` rutinas más parecidas, de la mejor a la peor. */
    fun topMatches(path: List<GeoPoint>, routines: List<Routine>, k: Int): List<RoutineMatch> {
        val queue = rank(path, routines, isPartial = true)
        return List(minOf(k, queue.size)) { queue.poll() }
    }

    private fun rank(path: List<GeoPoint>, routines: List<Routine>, isPartial: Boolean): PriorityQueue<RoutineMatch> {
        val queue = PriorityQueue<RoutineMatch>()
        routines.forEach { routine ->
            val routinePath = resampler.resample(routine.path)
            queue += RoutineMatch(routine, routinePath, comparator.compare(routinePath, path, isPartial))
        }
        return queue
    }

    private fun startsNear(routine: Routine, path: List<GeoPoint>): Boolean =
        isWithin(routine.path.firstOrNull(), path.firstOrNull(), thresholds.routineStartRadiusMeters)

    private fun endsNear(routine: Routine, path: List<GeoPoint>): Boolean =
        isWithin(routine.path.lastOrNull(), path.lastOrNull(), thresholds.routineEndRadiusMeters)

    private fun isWithin(a: GeoPoint?, b: GeoPoint?, radius: Double): Boolean =
        a != null && b != null && distanceCalculator.distance(a, b).value <= radius
}
