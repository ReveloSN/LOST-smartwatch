package com.lost.ai.scoring

import com.lost.ai.config.DetectionConfig
import com.lost.ai.config.ScoringWeights
import com.lost.ai.feedback.PersonalThresholdAdjuster
import com.lost.ai.fixtures.TripFixtures
import com.lost.ai.signals.DeviationSignal
import com.lost.ai.signals.SignalContext
import com.lost.ai.signals.SignalKind
import com.lost.ai.signals.SignalReading
import com.lost.domain.assistance.UserFeedback
import com.lost.domain.detection.AssistanceLevel
import com.lost.domain.model.PersonalThresholds
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.units.Probability
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ScoringTest {
    private val context = SignalContext(
        trip = TripFixtures.normalRoutineTrip(),
        path = emptyList(),
        routine = null,
        routinePath = emptyList(),
        target = null,
        progress = 0.0,
        sensitivity = Sensitivity.MEDIUM
    )

    private fun fixed(kind: SignalKind, score: Double) = object : DeviationSignal {
        override val kind = kind

        override fun measure(context: SignalContext) = SignalReading(kind, score)
    }

    private fun score(vararg signals: DeviationSignal) =
        CompositeAnomalyScorer(signals.toList(), DetectionConfig.DEFAULT.confusionWeights)
            .score(context, ScoringWeights.ROUTINE)

    @Test
    fun `el termino de interaccion separa desvio voluntario de desorientacion`() {
        val voluntary = score(fixed(SignalKind.OFF_ROUTE, 1.0), fixed(SignalKind.LOOP, 0.0))
        val disoriented = score(fixed(SignalKind.OFF_ROUTE, 1.0), fixed(SignalKind.LOOP, 1.0))
        assertTrue(voluntary.probability.value < PersonalThresholds.DEFAULT.medium)
        assertTrue(disoriented.probability.value > PersonalThresholds.DEFAULT.high)
    }

    @Test
    fun `una senal nueva se suma sin modificar el puntaje combinado`() {
        val withoutNew = score(fixed(SignalKind.OFF_ROUTE, 0.8))
        val withNew = score(fixed(SignalKind.OFF_ROUTE, 0.8), fixed(SignalKind.UNUSUAL_STOP, 1.0))
        assertTrue(withNew.probability > withoutNew.probability)
        assertEquals(1.0, withNew.confusion)
    }

    @Test
    fun `el clasificador usa los umbrales personales`() {
        val classifier = AssistanceLevelClassifier()
        val thresholds = PersonalThresholds(0.4, 0.7)
        assertEquals(AssistanceLevel.LOW, classifier.classify(Probability(0.39), thresholds))
        assertEquals(AssistanceLevel.MEDIUM, classifier.classify(Probability(0.4), thresholds))
        assertEquals(AssistanceLevel.HIGH, classifier.classify(Probability(0.7), thresholds))
    }

    @Test
    fun `estoy bien sube los umbrales y la ayuda confirmada los baja sin salirse de limites`() {
        val adjuster = PersonalThresholdAdjuster()
        val start = PersonalThresholds.DEFAULT
        val raised = adjuster.adjust(start, UserFeedback.IM_FINE)
        val lowered = adjuster.adjust(start, UserFeedback.HELP_CONFIRMED)
        assertTrue(raised.medium > start.medium && raised.high > start.high)
        assertTrue(lowered.medium < start.medium)
        var many = start
        repeat(50) { many = adjuster.adjust(many, UserFeedback.IM_FINE) }
        assertTrue(many.medium <= 0.75 && many.high <= 0.92 && many.medium < many.high)
    }
}
