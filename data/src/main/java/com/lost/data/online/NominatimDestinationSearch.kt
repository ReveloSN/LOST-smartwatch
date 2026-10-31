package com.lost.data.online

import com.lost.data.di.IoDispatcher
import com.lost.domain.error.LostError
import com.lost.domain.error.LostResult
import com.lost.domain.model.PlaceSuggestion
import com.lost.domain.port.DestinationSearchService
import java.io.IOException
import java.net.URLEncoder
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.json.JSONException

/**
 * Geocodificación con Nominatim (OpenStreetMap). Se llama una vez por búsqueda enviada,
 * nunca mientras se escribe, y solo viaja el texto buscado y el idioma.
 */
class NominatimDestinationSearch @Inject constructor(
    private val http: HttpGetClient,
    private val parser: NominatimResponseParser,
    @param:IoDispatcher private val io: CoroutineDispatcher
) : DestinationSearchService {
    override suspend fun search(query: String): LostResult<List<PlaceSuggestion>> = withContext(io) {
        val url = OsmUsagePolicy.SEARCH_URL.format(
            MAX_RESULTS,
            Locale.getDefault().language,
            URLEncoder.encode(query, Charsets.UTF_8.name())
        )
        try {
            LostResult.Success(parser.parse(http.get(url).decodeToString()))
        } catch (_: IOException) {
            LostResult.Failure(LostError.SearchUnavailable)
        } catch (_: JSONException) {
            LostResult.Failure(LostError.SearchUnavailable)
        }
    }

    private companion object {
        const val MAX_RESULTS = 3
    }
}
