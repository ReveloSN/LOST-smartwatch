package com.lost.data.online

import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.inject.Inject

/** GET con `HttpURLConnection` (sin librerías extra), User-Agent propio y tiempos de espera cortos. */
class UrlConnectionHttpClient @Inject constructor() : HttpGetClient {
    override fun get(url: String): ByteArray {
        val connection = URL(url).openConnection() as HttpURLConnection
        try {
            connection.setRequestProperty("User-Agent", OsmUsagePolicy.USER_AGENT)
            connection.connectTimeout = OsmUsagePolicy.CONNECT_TIMEOUT_MILLIS
            connection.readTimeout = OsmUsagePolicy.READ_TIMEOUT_MILLIS
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                throw IOException("HTTP ${connection.responseCode}")
            }
            return connection.inputStream.use { it.readBytes() }
        } finally {
            connection.disconnect()
        }
    }
}
