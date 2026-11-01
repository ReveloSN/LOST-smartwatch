package com.lost.watch.integration

import com.lost.watch.glance.GlanceSurfaces
import com.lost.watch.glance.WearGlanceSurfaces
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IntegrationModule {
    @Binds
    abstract fun monitoringController(impl: SessionMonitoringController): MonitoringController

    @Binds
    abstract fun glanceSurfaces(impl: WearGlanceSurfaces): GlanceSurfaces

    @Binds
    abstract fun promptNotifications(impl: AndroidPromptNotifications): PromptNotifications
}
