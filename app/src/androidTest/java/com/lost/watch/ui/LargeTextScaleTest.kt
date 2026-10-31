package com.lost.watch.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import org.junit.Rule
import org.junit.Test

/** Activar o desactivar Texto grande no debe borrar el estado de la pantalla (por ejemplo, la sección visible). */
class LargeTextScaleTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun cambiarTextoGrandeConservaElEstado() {
        var isLarge by mutableStateOf(false)
        compose.setContent {
            LargeTextScale(isEnabled = isLarge) {
                var taps by remember { mutableIntStateOf(0) }
                Button(onClick = { taps++ }) { Text("toques: $taps") }
            }
        }
        compose.onNodeWithText("toques: 0").performClick()
        compose.onNodeWithText("toques: 1").assertExists()

        isLarge = true
        compose.waitForIdle()

        compose.onNodeWithText("toques: 1").assertExists()
    }
}
