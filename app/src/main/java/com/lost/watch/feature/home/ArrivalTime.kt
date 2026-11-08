package com.lost.watch.feature.home

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.ui.formatMinuteOfDay
import com.lost.watch.ui.splitDayPeriod

/** La hora de llegada grande, con "a. m." / "p. m." más pequeño al lado para que nunca se corte. */
@Composable
internal fun ArrivalTime(minuteOfDay: Int) {
    val parts = splitDayPeriod(formatMinuteOfDay(minuteOfDay))
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        LostText(parts.time, modifier = Modifier.alignByBaseline(), role = LostTextRole.DISPLAY)
        parts.dayPeriod?.let {
            LostText(
                it,
                modifier = Modifier.alignByBaseline().padding(start = SemanticTokens.Spacing.xs),
                role = LostTextRole.BODY,
                color = SemanticTokens.Text.secondary
            )
        }
    }
}
