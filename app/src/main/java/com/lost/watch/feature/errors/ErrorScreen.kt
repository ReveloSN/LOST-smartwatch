package com.lost.watch.feature.errors

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.BatteryAlert
import androidx.compose.material.icons.rounded.LocationOff
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.ComponentTokens
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R
import com.lost.watch.ui.UiMessage

/**
 * 10 · Estado de error: qué pasó · por qué · qué hacer ahora. Lenguaje del usuario, nunca códigos.
 * El "por qué" solo aparece si ayuda a decidir.
 */
@Composable
fun ErrorScreen(message: UiMessage, icon: ImageVector) {
    val text = uiMessageText(message)
    WatchScreenTemplate(modifier = Modifier.semantics { liveRegion = LiveRegionMode.Polite }) {
        LostIcon(icon, null, tint = SemanticTokens.Status.deviation, size = ComponentTokens.Icon.large)
        LostText(text.whatHappened, role = LostTextRole.TITLE)
        text.why?.let { LostText(it, role = LostTextRole.CAPTION, color = SemanticTokens.Text.secondary) }
        LostText(text.whatToDo, role = LostTextRole.BODY, color = SemanticTokens.Status.onRoute)
    }
}

/** Icono de cada mensaje de error conocido. */
fun iconFor(message: UiMessage): ImageVector = when (message.whatHappened) {
    R.string.error_battery_what -> Icons.Rounded.BatteryAlert
    R.string.error_connection_what -> Icons.Rounded.WifiOff
    else -> Icons.Rounded.LocationOff
}

@LostDevicePreviews
@Composable
private fun GpsErrorPreview() {
    LostTheme {
        ErrorScreen(
            UiMessage(R.string.error_gps_what, R.string.error_gps_why, R.string.error_gps_todo),
            Icons.Rounded.LocationOff
        )
    }
}

@LostDevicePreviews
@Composable
private fun BatteryErrorPreview() {
    LostTheme {
        ErrorScreen(
            UiMessage(R.string.error_battery_what, null, R.string.error_battery_todo, 10),
            Icons.Rounded.BatteryAlert
        )
    }
}
