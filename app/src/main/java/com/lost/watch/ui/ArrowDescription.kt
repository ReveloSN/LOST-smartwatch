package com.lost.watch.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.lost.watch.R

/** "Flecha que apunta al noreste": la flecha de guía también se escucha. */
@Composable
fun arrowDescription(bearingDegrees: Float): String =
    stringResource(R.string.cd_navigation_arrow, stringResource(CompassDirection.of(bearingDegrees).label))
