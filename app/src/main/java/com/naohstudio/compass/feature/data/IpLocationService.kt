package com.naohstudio.compass.feature.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Approximate location from the device's public IP, so true north can be computed
 * without requesting the location permission. City-level accuracy is plenty for
 * magnetic declination, which changes very slowly across distance.
 */
object IpLocationService {

    private const val TAG = "IpLocationService"
    private const val TIMEOUT_MS = 5000

    data class IpLocation(val latitude: Double, val longitude: Double)

    suspend fun fetch(): IpLocation? = withContext(Dispatchers.IO) {
        fetchFrom("https://ipwho.is/", "latitude", "longitude")
            ?: fetchFrom("https://ipapi.co/json/", "latitude", "longitude")
    }

    private fun fetchFrom(url: String, latKey: String, lonKey: String): IpLocation? {
        var connection: HttpURLConnection? = null
        return try {
            connection = (URL(url).openConnection() as HttpURLConnection).apply {
                connectTimeout = TIMEOUT_MS
                readTimeout = TIMEOUT_MS
                setRequestProperty("Accept", "application/json")
            }
            if (connection.responseCode != HttpURLConnection.HTTP_OK) return null
            val json = JSONObject(connection.inputStream.bufferedReader().use { it.readText() })
            if (!json.has(latKey) || !json.has(lonKey)) return null
            IpLocation(json.getDouble(latKey), json.getDouble(lonKey))
        } catch (ex: Exception) {
            Log.d(TAG, "fetch $url failed: ${ex.message}")
            null
        } finally {
            connection?.disconnect()
        }
    }
}
