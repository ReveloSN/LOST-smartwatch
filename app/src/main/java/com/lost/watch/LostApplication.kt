package com.lost.watch

import android.app.Application
import com.lost.data.demo.DemoDataSeeder
import com.lost.data.di.ApplicationScope
import com.lost.watch.glance.GlanceRefresher
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class LostApplication : Application() {
    @Inject lateinit var demoDataSeeder: DemoDataSeeder

    @Inject lateinit var glanceRefresher: GlanceRefresher

    @Inject @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { demoDataSeeder.seedIfNeeded() }
        applicationScope.launch { glanceRefresher.run() }
    }
}
