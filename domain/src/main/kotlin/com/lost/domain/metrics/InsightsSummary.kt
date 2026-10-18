package com.lost.domain.metrics

/**
 * Métricas de la semana para la pantalla Insights. Los valores son `null` cuando
 * todavía no hay datos (no se inventa un 0 % ni un 100 %).
 */
data class InsightsSummary(
    val successRate: Double?,
    val averageDecisionSeconds: Double?,
    val averageRecoverySeconds: Double?,
    val falsePositiveRate: Double?,
    val alertsShown: Int,
    val routinesLearned: Int,
    val tripsCompleted: Int
) {
    val meetsSuccessGoal: Boolean get() = (successRate ?: 0.0) >= SUCCESS_GOAL
    val meetsDecisionGoal: Boolean get() = (averageDecisionSeconds ?: Double.MAX_VALUE) < DECISION_GOAL_SECONDS
    val meetsRecoveryGoal: Boolean get() = (averageRecoverySeconds ?: Double.MAX_VALUE) < RECOVERY_GOAL_SECONDS
    val meetsFalsePositiveGoal: Boolean get() = (falsePositiveRate ?: 1.0) < FALSE_POSITIVE_GOAL

    companion object {
        const val SUCCESS_GOAL = 0.9
        const val DECISION_GOAL_SECONDS = 5.0
        const val RECOVERY_GOAL_SECONDS = 30.0
        const val FALSE_POSITIVE_GOAL = 0.1
    }
}
