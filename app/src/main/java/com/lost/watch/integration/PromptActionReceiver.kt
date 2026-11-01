package com.lost.watch.integration

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.lost.domain.session.TripSession
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/** Recibe "Estoy bien" o "Voy a otro lado" tocados en la notificación y se los da a la sesión. */
class PromptActionReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        val action = PromptAction.from(intent) ?: return
        EntryPointAccessors.fromApplication(context, SessionEntryPoint::class.java).session().dispatch(action.event)
    }

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface SessionEntryPoint {
        fun session(): TripSession
    }
}
