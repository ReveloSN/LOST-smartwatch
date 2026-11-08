package com.lost.watch.ui

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

/**
 * Pide permisos y devuelve la acción para el botón. Si Android ya no muestra el diálogo (la persona lo negó
 * dos veces), el botón no puede quedar mudo: abre los ajustes de LOST para darlo desde ahí.
 */
@Composable
fun rememberPermissionRequest(permissions: Array<String>, onAnswered: () -> Unit): () -> Unit {
    val context = LocalContext.current
    val activity = LocalActivity.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        // Un resultado vacío es un pedido cancelado, no una negativa: ahí no se manda a nadie a los ajustes.
        val wasDenied = result.isNotEmpty() && result.values.none { it }
        if (wasDenied && activity != null && !canAskAgain(activity, permissions)) openAppSettings(context)
        onAnswered()
    }
    return { launcher.launch(permissions) }
}

private fun canAskAgain(activity: Activity, permissions: Array<String>): Boolean =
    permissions.any(activity::shouldShowRequestPermissionRationale)

private fun openAppSettings(context: Context) {
    val intent =
        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", context.packageName, null))
    runCatching { context.startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
}
