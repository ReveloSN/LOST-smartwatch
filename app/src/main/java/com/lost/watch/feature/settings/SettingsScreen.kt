package com.lost.watch.feature.settings

import android.content.Intent
import android.os.Build
import android.provider.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.PlayCircle
import androidx.compose.material.icons.rounded.TextFields
import androidx.compose.material.icons.rounded.Translate
import androidx.compose.material.icons.rounded.Tune
import androidx.compose.material.icons.rounded.Vibration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.lost.designsystem.components.molecules.ListItemContent
import com.lost.designsystem.components.molecules.LostListItem
import com.lost.designsystem.components.templates.ListTemplate
import com.lost.designsystem.preview.LostDevicePreviews
import com.lost.designsystem.theme.LostTheme
import com.lost.domain.model.Sensitivity
import com.lost.domain.model.VibrationIntensity
import com.lost.watch.R
import com.lost.watch.navigation.LostDestination

/** 04.1 · Ajustes en lista circular. Contactos y Tu semana viven en su propia sección (sin duplicar entradas). */
@Composable
fun SettingsScreen(onOpen: (LostDestination) -> Unit, viewModel: SettingsViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    SettingsContent(state, onOpen, viewModel::onEvent) {
        val intent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Intent(
                Settings.ACTION_APP_LOCALE_SETTINGS
            ).setData(android.net.Uri.fromParts("package", context.packageName, null))
        } else {
            Intent(Settings.ACTION_LOCALE_SETTINGS)
        }
        runCatching { context.startActivity(intent) }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState,
    onOpen: (LostDestination) -> Unit,
    onEvent: (SettingsUiEvent) -> Unit,
    onOpenLanguage: () -> Unit
) {
    val sensitivity = stringResource(SettingsLabels.of(state.sensitivity))
    val vibration = stringResource(SettingsLabels.of(state.vibration))
    val largeText = stringResource(SettingsLabels.onOff(state.isLargeTextEnabled))
    val language = LocalConfiguration.current.locales[0].let {
        it.getDisplayLanguage(it)
    }.replaceFirstChar { it.titlecase() }
    ListTemplate(title = stringResource(R.string.nav_settings)) {
        item {
            Row(Icons.Rounded.Tune, R.string.settings_sensitivity, sensitivity) { onOpen(LostDestination.Sensitivity) }
        }
        item {
            Row(Icons.Rounded.Vibration, R.string.settings_vibration, vibration) {
                onEvent(SettingsUiEvent.ToggleVibration)
            }
        }
        item {
            Row(Icons.Rounded.TextFields, R.string.settings_large_text, largeText) {
                onEvent(SettingsUiEvent.ToggleLargeText)
            }
        }
        item { Row(Icons.Rounded.Translate, R.string.settings_language, language, onOpenLanguage) }
        item { Row(Icons.Rounded.Lock, R.string.settings_privacy, null) { onOpen(LostDestination.Privacy) } }
        if (state.isDemoAvailable) {
            item { Row(Icons.Rounded.PlayCircle, R.string.settings_demo, null) { onOpen(LostDestination.Demo) } }
        }
    }
}

@Composable
private fun Row(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: Int,
    value: String?,
    onClick: () -> Unit
) {
    LostListItem(ListItemContent(icon, stringResource(title), value), onClick = onClick)
}

@LostDevicePreviews
@Composable
private fun SettingsPreview() {
    val state =
        SettingsUiState(
            Sensitivity.MEDIUM,
            VibrationIntensity.STRONG,
            false,
            35,
            isDemoAvailable = true,
            hasDeletedLearning = false
        )
    LostTheme { SettingsContent(state, {}, {}, {}) }
}
