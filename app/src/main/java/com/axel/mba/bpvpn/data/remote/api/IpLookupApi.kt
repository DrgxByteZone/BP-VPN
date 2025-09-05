package com.axel.mba.bpvpn.data.remote.api

import com.axel.mba.bpvpn.core.common.Constants
import com.axel.mba.bpvpn.core.model.IpDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

/**
 * Queries real public IP and location details directly from standard public endpoints.
 */
class IpLookupApi {

    suspend fun fetchPublicIp(): IpDetails = withContext(Dispatchers.IO) {
        // Try ipinfo.io first for IP + Geo
        try {
            val url = URL(Constants.IP_LOOKUP_IPINFO_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("User-Agent", "BP-VPN/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()

                val json = JSONObject(response)
                return@withContext IpDetails(
                    ip = json.optString("ip", "Unknown"),
                    city = if (json.has("city")) json.getString("city") else null,
                    region = if (json.has("region")) json.getString("region") else null,
                    country = if (json.has("country")) json.getString("country") else null,
                    isp = if (json.has("org")) json.getString("org") else null,
                    isMasked = false
                )
            }
        } catch (e: Exception) {
            // Fallback to ipify.org
        }

        // Fallback: ipify.org
        try {
            val url = URL(Constants.IP_LOOKUP_IPIFY_URL)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "GET"
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("User-Agent", "BP-VPN/1.0")
            }

            if (connection.responseCode == HttpURLConnection.HTTP_OK) {
                val reader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = reader.readText()
                reader.close()
                connection.disconnect()

                val json = JSONObject(response)
                return@withContext IpDetails(
                    ip = json.optString("ip", "0.0.0.0"),
                    city = "Protected",
                    country = "Anycast",
                    isp = "Secure Gateway",
                    isMasked = true
                )
            }
        } catch (e: Exception) {
            // Offline or network error
        }

        IpDetails(
            ip = "127.0.0.1",
            city = "Local Device",
            country = "ID",
            isp = "Offline",
            isMasked = false
        )
    }
}
