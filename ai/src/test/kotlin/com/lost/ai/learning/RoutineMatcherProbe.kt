package com.lost.ai.learning

import com.lost.ai.config.DetectionThresholds
import com.lost.ai.geometry.TrajectoryResampler
import com.lost.ai.sequence.DtwSequenceComparator
import com.lost.domain.model.GeoPoint
import com.lost.domain.model.Routine

/** Construye un [RoutineMatcher] con la configuración por defecto para probarlo aislado. */
class RoutineMatcherProbe {
    private val thresholds = DetectionThresholds.DEFAULT
    private val resampler = TrajectoryResampler(thresholds.resampleSpacingMeters)
    private val matcher = RoutineMatcher(
        DtwSequenceComparator(thresholds.dtwBandRatio, thresholds.dtwMinBandPoints),
        resampler,
        thresholds
    )

    fun best(points: List<GeoPoint>, routines: List<Routine>): String? =
        matcher.bestOngoingMatch(resampler.resample(points), routines)?.routine?.id
}
