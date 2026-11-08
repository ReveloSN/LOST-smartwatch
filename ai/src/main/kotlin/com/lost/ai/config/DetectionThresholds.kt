package com.lost.ai.config

/**
 * Todos los umbrales del motor en un solo lugar (nada de números mágicos en las señales).
 * Las distancias están en metros y los tiempos en segundos salvo que el nombre diga otra cosa.
 */
data class DetectionThresholds(
    val resampleSpacingMeters: Double,
    /** Un punto aislado que implicaría ir más rápido que esto (m/s) se toma como salto de la señal. */
    val gpsJumpSpeedMps: Double,
    /** Cada punto GPS se promedia con los de estos últimos segundos (el temblor del reloj baja a la mitad). */
    val gpsSmoothingSeconds: Double,
    val dtwBandRatio: Double,
    val dtwMinBandPoints: Int,
    val routineStartRadiusMeters: Double,
    val routineEndRadiusMeters: Double,
    val routineMatchMaxCostMeters: Double,
    val routineConfirmationRepetitions: Int,
    val offRouteToleranceMeters: Double,
    val offRouteSaturationMeters: Double,
    val divergenceToleranceMeters: Double,
    val divergenceSaturationMeters: Double,
    val loopCellMeters: Double,
    val loopMinPointsBetweenPasses: Int,
    val loopSaturationPasses: Int,
    val headingWindowSize: Int,
    /** Tramos remuestreados a cada lado de un punto para medir su giro (2 = 40 m). */
    val headingChordPoints: Int,
    val sharpTurnDegrees: Double,
    val erraticBaselineRatio: Double,
    val erraticSaturationRatio: Double,
    val movingAwayMinMeters: Double,
    val movingAwaySaturationMeters: Double,
    val stopRadiusMeters: Double,
    val stopMinSeconds: Double,
    val stopSaturationSeconds: Double,
    val stopIgnoreNearStartMeters: Double,
    val delayToleranceShare: Double,
    val reasonMinScore: Double
) {
    companion object {
        val DEFAULT = DetectionThresholds(
            resampleSpacingMeters = 20.0,
            gpsJumpSpeedMps = 5.0,
            gpsSmoothingSeconds = 15.0,
            dtwBandRatio = 0.25,
            dtwMinBandPoints = 5,
            routineStartRadiusMeters = 150.0,
            routineEndRadiusMeters = 150.0,
            routineMatchMaxCostMeters = 60.0,
            routineConfirmationRepetitions = 5,
            offRouteToleranceMeters = 40.0,
            offRouteSaturationMeters = 200.0,
            divergenceToleranceMeters = 25.0,
            divergenceSaturationMeters = 150.0,
            loopCellMeters = 20.0,
            loopMinPointsBetweenPasses = 4,
            loopSaturationPasses = 3,
            headingWindowSize = 8,
            headingChordPoints = 2,
            sharpTurnDegrees = 60.0,
            erraticBaselineRatio = 0.25,
            erraticSaturationRatio = 0.5,
            movingAwayMinMeters = 30.0,
            movingAwaySaturationMeters = 200.0,
            stopRadiusMeters = 15.0,
            stopMinSeconds = 60.0,
            stopSaturationSeconds = 180.0,
            stopIgnoreNearStartMeters = 50.0,
            delayToleranceShare = 1.0,
            reasonMinScore = 0.3
        )
    }
}
