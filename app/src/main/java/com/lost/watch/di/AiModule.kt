package com.lost.watch.di

import com.lost.ai.LostEngineAssembly
import com.lost.ai.feedback.PersonalThresholdAdjuster
import com.lost.domain.port.GuidanceCalculator
import com.lost.domain.port.IdGenerator
import com.lost.domain.port.RoutineLearningEngine
import com.lost.domain.port.ThresholdAdjuster
import com.lost.domain.port.TripEvaluator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/** Conecta el motor de IA (Kotlin puro, sin Hilt) con los puertos del dominio. */
@Module
@InstallIn(SingletonComponent::class)
object AiModule {
    private val assembly = LostEngineAssembly()

    @Provides
    @Singleton
    fun tripEvaluator(): TripEvaluator = assembly.detectionEngine()

    @Provides
    @Singleton
    fun routineLearningEngine(idGenerator: IdGenerator): RoutineLearningEngine = assembly.routineLearner(idGenerator)

    @Provides
    fun thresholdAdjuster(): ThresholdAdjuster = PersonalThresholdAdjuster()

    @Provides
    fun guidanceCalculator(): GuidanceCalculator = assembly.guidanceCalculator()
}
