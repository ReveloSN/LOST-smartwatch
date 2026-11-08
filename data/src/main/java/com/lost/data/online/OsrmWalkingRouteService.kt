package com.lost.data.online

import com.lost.data.di.IoDispatcher
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.GeoPoint
import com.lost.domain.port.WalkingRouteService
import java.io.IOException
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONException

/** Ruta a pie por calles reales con OSRM (perfil "foot" del servidor de OpenStreetMap Alemania). */
class OsrmWalkingRouteService @Inject constructor(
    private val http: HttpGetClient,
    private val parser: OsrmRouteParser,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : WalkingRouteService {
    override suspend fun walkingRoute(from: GeoPoint, to: GeoPoint): LostResult<List<GeoPoint>> = withContext(io) {
        val url = OsmUsagePolicy.WALKING_ROUTE_URL.format(
            coordinate(from.longitude),
            coordinate(from.latitude),
            coordinate(to.longitude),
            coordinate(to.latitude)
        )
        try {
            val route = parser.parse(http.get(url).decodeToString())
            if (route.isEmpty()) LostResult.Failure(LostError.MapUnavailable) else LostResult.Success(route)
        } catch (_: IOException) {
            LostResult.Failure(LostError.MapUnavailable)
        } catch (_: JSONException) {
            LostResult.Failure(LostError.MapUnavailable)
        }
    }

    private fun coordinate(value: Double) = String.format(Locale.ROOT, "%.6f", value)
}
