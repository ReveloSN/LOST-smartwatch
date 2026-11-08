package com.lost.watch.feature.settings

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Map
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.designsystem.components.atoms.LostIcon
import com.lost.designsystem.components.atoms.LostText
import com.lost.designsystem.components.atoms.LostTextRole
import com.lost.designsystem.components.templates.WatchScreenTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens
import com.lost.watch.R

/** Ajustes → Acerca de: de dónde salen el mapa, la búsqueda y las rutas, con la atribución de OSM. */
@Composable
fun AboutScreen() {
    WatchScreenTemplate {
        LostIcon(Icons.Rounded.Map, null, tint = SemanticTokens.Status.onRoute)
        LostText(stringResource(R.string.about_map), role = LostTextRole.TITLE)
        LostText(stringResource(R.string.map_attribution), role = LostTextRole.CAPTION)
        LostText(
            stringResource(R.string.about_license),
            role = LostTextRole.CAPTION,
            color = SemanticTokens.Text.secondary
        )
    }
}

@LostDevicePreviews
@Composable
private fun AboutPreview() {
    LostTheme { AboutScreen() }
}
