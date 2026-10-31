package com.lost.data.di

import android.content.Context
import com.lost.data.map.CachedMapTileSource
import com.lost.data.map.NetworkTileSource
import com.lost.data.map.TileCachePolicy
import com.lost.data.map.TileDiskCache
import com.lost.data.online.HttpGetClient
import com.lost.data.online.NominatimDestinationSearch
import com.lost.data.online.OsrmWalkingRouteService
import com.lost.data.online.UrlConnectionHttpClient
import com.lost.domain.map.LruCache
import com.lost.domain.port.DestinationSearchService
import com.lost.domain.port.MapTileSource
import com.lost.domain.port.WalkingRouteService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

/** Mapa real (OpenStreetMap): teselas con caché en memoria y disco, búsqueda de lugares y ruta a pie. */
@Module
@InstallIn(SingletonComponent::class)
object OnlineMapModule {
    @Provides
    @IoDispatcher
    fun ioDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    fun httpClient(impl: UrlConnectionHttpClient): HttpGetClient = impl

    @Provides
    @Singleton
    fun mapTileSource(
        @ApplicationContext context: Context,
        network: NetworkTileSource,
        @IoDispatcher io: CoroutineDispatcher
    ): MapTileSource {
        val policy = TileCachePolicy.DEFAULT
        val disk = TileDiskCache(File(context.cacheDir, TILE_DIRECTORY), policy, System::currentTimeMillis)
        return CachedMapTileSource(LruCache(policy.memoryEntries), disk, network, io)
    }

    @Provides
    fun destinationSearch(impl: NominatimDestinationSearch): DestinationSearchService = impl

    @Provides
    fun walkingRoute(impl: OsrmWalkingRouteService): WalkingRouteService = impl

    private const val TILE_DIRECTORY = "osm-tiles"
}
