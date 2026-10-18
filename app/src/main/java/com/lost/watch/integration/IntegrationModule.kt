package com.lost.watch.integration

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class IntegrationModule {
    @Binds
    abstract fun monitoringController(impl: SessionMonitoringController): MonitoringController
}
