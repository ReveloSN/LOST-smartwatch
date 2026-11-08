package com.lost.watch.feature.insights

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/** 13 · Métricas de éxito (segundo nivel de Tu semana): una métrica por fila, con su meta. */
@Composable
fun MetricsScreen(viewModel: InsightsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    state?.let { MetricsContent(it.metrics) }
}

@Composable
private fun MetricsContent(metrics: List<MetricLine>) {
    ListTemplate(title = stringResource(R.string.insights_metrics_title)) {
        metrics.forEach { line -> item(key = line.label) { MetricRow(line) } }
    }
}

@Composable
private fun MetricRow(line: MetricLine) {
    val goal = if (line.meetsGoal) {
        stringResource(R.string.insights_goal_met)
    } else {
        stringResource(R.string.insights_goal, stringResource(line.goal))
    }
    Column(
        modifier = Modifier.fillMaxWidth().padding(vertical = SemanticTokens.Spacing.sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SemanticTokens.Spacing.hairline)
    ) {
        LostText(stringResource(line.label), role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
        LostText(
            metricValue(line.value),
            role = LostTextRole.TITLE,
            color = if (line.meetsGoal) SemanticTokens.Status.onRoute else SemanticTokens.Text.primary
        )
        LostText(goal, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary)
    }
}

@Composable
private fun metricValue(value: MetricValue): String = when (value) {
    is MetricValue.Percent -> stringResource(R.string.insights_percent, value.value)

    is MetricValue.Seconds -> stringResource(
        R.string.insights_seconds,
        String.format(LocalConfiguration.current.locales[0], "%.1f", value.value)
    )

    MetricValue.NoData -> stringResource(R.string.insights_no_data)
}

@LostDevicePreviews
@Composable
private fun MetricsPreview() {
    val metrics = listOf(
        MetricLine(R.string.insights_success, MetricValue.Percent(92), true, R.string.insights_goal_success),
        MetricLine(R.string.insights_decision, MetricValue.NoData, false, R.string.insights_goal_decision)
    )
    LostTheme { MetricsContent(metrics) }
}
