package com.lost.designsystem.preview

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.lost.designsystem.theme.LostTheme
import com.lost.designsystem.tokens.SemanticTokens

/** Fondo negro OLED + tema, para que cada preview se vea como en el reloj. */
@Composable
fun LostPreviewSurface(content: @Composable () -> Unit) {
    LostTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SemanticTokens.Background.base)
                .padding(SemanticTokens.Spacing.xl),
            contentAlignment = Alignment.Center
        ) {
            content()
        }
    }
}
