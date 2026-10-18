package com.lost.data.di

import com.lost.data.contacts.ActiveContactNotifier
import com.lost.data.contacts.AuthorizedContactRepository
import com.lost.data.demo.AndroidGpxAssetOpener
import com.lost.data.demo.GpxAssetOpener
import com.lost.data.haptics.AndroidHapticNotifier
import com.lost.data.location.ActiveLocationSource
import com.lost.data.metrics.LocalMetricsTracker
import com.lost.data.persistence.RoomDestinationRepository
import com.lost.data.persistence.RoomRoutineRepository
import com.lost.data.persistence.RoomTripRepository
import com.lost.data.preferences.DataStoreSettingsRepository
import com.lost.data.system.AndroidDeviceStatusReader
import com.lost.data.system.SystemTimeSource
import com.lost.data.system.UuidIdGenerator
import com.lost.domain.port.ContactNotifier
import com.lost.domain.port.ContactReader
import com.lost.domain.port.ContactWriter
import com.lost.domain.port.DestinationReader
import com.lost.domain.port.DestinationWriter
import com.lost.domain.port.DeviceStatusReader
import com.lost.domain.port.HapticNotifier
import com.lost.domain.port.IdGenerator
import com.lost.domain.port.LocationSource
import com.lost.domain.port.MetricsReader
import com.lost.domain.port.MetricsTracker
import com.lost.domain.port.RoutineReader
import com.lost.domain.port.RoutineWriter
import com.lost.domain.port.SettingsReader
import com.lost.domain.port.SettingsWriter
import com.lost.domain.port.TimeSource
import com.lost.domain.port.TripReader
import com.lost.domain.port.TripRecorder
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/** Inversión de dependencias: conecta cada puerto del dominio con su implementación. */
@Suppress("TooManyFunctions")
@Module
@InstallIn(SingletonComponent::class)
abstract class DataBindingsModule {
    @Binds abstract fun tripRecorder(impl: RoomTripRepository): TripRecorder

    @Binds abstract fun tripReader(impl: RoomTripRepository): TripReader

    @Binds abstract fun routineReader(impl: RoomRoutineRepository): RoutineReader

    @Binds abstract fun routineWriter(impl: RoomRoutineRepository): RoutineWriter

    @Binds abstract fun destinationReader(impl: RoomDestinationRepository): DestinationReader

    @Binds abstract fun destinationWriter(impl: RoomDestinationRepository): DestinationWriter

    @Binds abstract fun metricsTracker(impl: LocalMetricsTracker): MetricsTracker

    @Binds abstract fun metricsReader(impl: LocalMetricsTracker): MetricsReader

    @Binds abstract fun settingsReader(impl: DataStoreSettingsRepository): SettingsReader

    @Binds abstract fun settingsWriter(impl: DataStoreSettingsRepository): SettingsWriter

    @Binds abstract fun contactReader(impl: AuthorizedContactRepository): ContactReader

    @Binds abstract fun contactWriter(impl: AuthorizedContactRepository): ContactWriter

    @Binds abstract fun contactNotifier(impl: ActiveContactNotifier): ContactNotifier

    @Binds abstract fun hapticNotifier(impl: AndroidHapticNotifier): HapticNotifier

    @Binds abstract fun locationSource(impl: ActiveLocationSource): LocationSource

    @Binds abstract fun timeSource(impl: SystemTimeSource): TimeSource

    @Binds abstract fun idGenerator(impl: UuidIdGenerator): IdGenerator

    @Binds abstract fun deviceStatusReader(impl: AndroidDeviceStatusReader): DeviceStatusReader

    @Binds abstract fun gpxAssetOpener(impl: AndroidGpxAssetOpener): GpxAssetOpener
}
