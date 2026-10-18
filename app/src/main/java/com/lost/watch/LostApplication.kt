package com.lost.watch

import android.app.Application
import com.lost.data.demo.DemoDataSeeder
import com.lost.data.di.ApplicationScope
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@HiltAndroidApp
class LostApplication : Application() {
    @Inject lateinit var demoDataSeeder: DemoDataSeeder

    @Inject @ApplicationScope
    lateinit var applicationScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        applicationScope.launch { demoDataSeeder.seedIfNeeded() }
    }
}
