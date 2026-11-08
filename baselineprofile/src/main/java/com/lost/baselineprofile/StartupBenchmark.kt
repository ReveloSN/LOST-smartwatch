package com.lost.baselineprofile

import androidx.benchmark.macro.BaselineProfileMode
import androidx.benchmark.macro.CompilationMode
import androidx.benchmark.macro.FrameTimingMetric
import androidx.benchmark.macro.StartupMode
import androidx.benchmark.macro.StartupTimingMetric
import androidx.benchmark.macro.junit4.MacrobenchmarkRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Arranque en frío y paso por las secciones: sin perfil frente a con el Baseline Profile. */
@RunWith(AndroidJUnit4::class)
class StartupBenchmark {
    @get:Rule
    val rule = MacrobenchmarkRule()

    @Test
    fun sinPerfil() = measure(CompilationMode.None())

    @Test
    fun conBaselineProfile() = measure(CompilationMode.Partial(BaselineProfileMode.Require))

    private fun measure(compilation: CompilationMode) = rule.measureRepeated(
        packageName = PACKAGE,
        metrics = listOf(StartupTimingMetric(), FrameTimingMetric()),
        compilationMode = compilation,
        startupMode = StartupMode.COLD,
        iterations = ITERATIONS
    ) {
        openAndVisitSections()
    }

    private companion object {
        const val ITERATIONS = 5
    }
}
