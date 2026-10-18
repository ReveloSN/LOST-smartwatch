package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import com.lost.domain.detection.DeviationReason
import com.lost.watch.R

/**
 * "Te saliste de tu ruta de siempre y pasaste 3 veces por la misma cuadra."
 * Une las dos razones más fuertes en una frase humana, sin porcentajes.
 */
@Composable
fun explanationSentence(reasons: List<DeviationReason>): String {
    val phrases = reasons.take(MAX_REASONS).map { reasonPhrase(it) }
    val locale = LocalConfiguration.current.locales[0]
    val sentence = when (phrases.size) {
        0 -> return stringResource(R.string.explanation_none)
        1 -> stringResource(R.string.explanation_one, phrases[0])
        else -> stringResource(R.string.explanation_two, phrases[0], phrases[1])
    }
    return sentence.replaceFirstChar { it.titlecase(locale) }
}

@Composable
private fun reasonPhrase(reason: DeviationReason): String = when (reason) {
    is DeviationReason.LeftUsualRoute -> stringResource(R.string.phrase_left_route)

    is DeviationReason.RepeatedArea -> pluralStringResource(R.plurals.phrase_repeated, reason.times, reason.times)

    is DeviationReason.TakingLonger ->
        pluralStringResource(R.plurals.phrase_taking_longer, reason.extraMinutes, reason.extraMinutes)

    DeviationReason.MovingAwayFromDestination -> stringResource(R.string.phrase_moving_away)

    DeviationReason.ErraticDirection -> stringResource(R.string.phrase_erratic)

    DeviationReason.UnusualStop -> stringResource(R.string.phrase_unusual_stop)
}

private const val MAX_REASONS = 2
