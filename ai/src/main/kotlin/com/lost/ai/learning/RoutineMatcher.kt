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
 * La ruta remuestreada de cada rutina se guarda: se pide en cada punto GPS y no cambia mientras la rutina
 * no cambie. Además, entregar siempre la misma lista deja que el DTW retome sus filas (`DtwRowCache`).
 */
class RoutineMatcher(
    private val comparator: SequenceComparator,
    private val resampler: TrajectoryResampler,
    private val thresholds: DetectionThresholds,
    private val distanceCalculator: HaversineDistanceCalculator = HaversineDistanceCalculator()
) {
    private val resampledRoutes = HashMap<String, Pair<List<GeoPoint>, List<GeoPoint>>>()

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
            val routinePath = resampledPathOf(routine)
            queue += RoutineMatch(routine, routinePath, comparator.compare(routinePath, path, isPartial))
        }
        return queue
    }

    @Synchronized
    private fun resampledPathOf(routine: Routine): List<GeoPoint> {
        val cached = resampledRoutes[routine.id]
        if (cached != null && (cached.first === routine.path || cached.first == routine.path)) return cached.second
        // Las rutinas borradas no se avisan: con un tope, el caché nunca crece sin límite.
        if (resampledRoutes.size >= CACHED_ROUTES) resampledRoutes.clear()
        return resampler.resample(routine.path).also { resampledRoutes[routine.id] = routine.path to it }
    }

    private fun startsNear(routine: Routine, path: List<GeoPoint>): Boolean =
        isWithin(routine.path.firstOrNull(), path.firstOrNull(), thresholds.routineStartRadiusMeters)

    private fun endsNear(routine: Routine, path: List<GeoPoint>): Boolean =
        isWithin(routine.path.lastOrNull(), path.lastOrNull(), thresholds.routineEndRadiusMeters)

    private fun isWithin(a: GeoPoint?, b: GeoPoint?, radius: Double): Boolean =
        a != null && b != null && distanceCalculator.distance(a, b).value <= radius

    private companion object {
        const val CACHED_ROUTES = 32
    }
}
