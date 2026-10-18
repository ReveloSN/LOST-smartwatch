package com.lost.ai.config

/** Cuánto se mueven los umbrales personales con cada respuesta del usuario, y sus límites. */
data class FeedbackSteps(
    val imFineStep: Double,
    val goingElsewhereStep: Double,
    val helpConfirmedStep: Double,
    val minMedium: Double,
    val maxMedium: Double,
    val maxHigh: Double,
    val minGap: Double
) {
    companion object {
        val DEFAULT = FeedbackSteps(
            imFineStep = 0.05,
            goingElsewhereStep = 0.02,
            helpConfirmedStep = 0.03,
            minMedium = 0.3,
            maxMedium = 0.75,
            maxHigh = 0.92,
            minGap = 0.15
        )
    }
}
