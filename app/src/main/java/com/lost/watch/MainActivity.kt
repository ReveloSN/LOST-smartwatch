package com.lost.watch

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.lost.domain.session.TripSession
import com.lost.watch.integration.AppVisibility
import com.lost.watch.integration.PromptAction
import com.lost.watch.navigation.LostApp
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject lateinit var visibility: AppVisibility

    @Inject lateinit var session: TripSession

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) answerFromNotification(intent)
        setContent { LostApp() }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        answerFromNotification(intent)
    }

    override fun onStart() {
        super.onStart()
        visibility.update(isVisible = true)
    }

    override fun onStop() {
        visibility.update(isVisible = false)
        super.onStop()
    }

    /** "Guíame" tocado en la notificación abre la app con la respuesta: la guía se ve aquí. */
    private fun answerFromNotification(intent: Intent?) {
        PromptAction.from(intent)?.let { session.dispatch(it.event) }
        intent?.removeExtra(PromptAction.EXTRA)
    }
}
