package com.lost.designsystem.theme

import androidx.compose.runtime.Composable
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Typography
import com.lost.designsystem.tokens.SemanticTokens

/**
 * Tema único de LOST: fondo negro OLED y acento aqua. Conecta los tokens semánticos
 * con Material 3 para Wear OS, así los componentes de la librería también respetan la marca.
 */
@Composable
fun LostTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lostColorScheme,
        typography = lostMaterialTypography,
        content = content
    )
}

private val lostColorScheme = ColorScheme(
    primary = SemanticTokens.Status.onRoute,
    onPrimary = SemanticTokens.Text.onAccent,
    secondary = SemanticTokens.Status.deviation,
    error = SemanticTokens.Status.help,
    background = SemanticTokens.Background.base,
    onBackground = SemanticTokens.Text.primary,
    onSurface = SemanticTokens.Text.primary,
    onSurfaceVariant = SemanticTokens.Text.secondary,
    surfaceContainer = SemanticTokens.Background.raised,
    outline = SemanticTokens.Surface.outline
)

private val lostMaterialTypography = Typography(
    displayLarge = LostTypography.display,
    displayMedium = LostTypography.number,
    titleLarge = LostTypography.question,
    titleMedium = LostTypography.title,
    bodyLarge = LostTypography.body,
    bodyMedium = LostTypography.body,
    labelMedium = LostTypography.button,
    labelSmall = LostTypography.caption
)
