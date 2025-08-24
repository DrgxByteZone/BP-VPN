package com.axel.mba.bpvpn.core.network.dns

import com.axel.mba.bpvpn.core.common.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Encrypted DNS-over-HTTPS (DoH) client using RFC 8484 wireformat.
 * Queries Cloudflare 1.1.1.1 / Quad9 over TLS to prevent ISP DNS hijacking and censorship.
 */
class DoHClient(
    private val endpointUrl: String = Constants.DOH_CLOUDFLARE_URL
) {

    suspend fun resolve(dnsQueryBytes: ByteArray): ByteArray? = withContext(Dispatchers.IO) {
        var connection: HttpURLConnection? = null
        try {
            val url = URL(endpointUrl)
            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                useCaches = false
                connectTimeout = 4000
                readTimeout = 4000
                setRequestProperty("Content-Type", "application/dns-message")
                setRequestProperty("Accept", "application/dns-message")
                setRequestProperty("User-Agent", "BP-VPN/1.0")
            }

            connection.outputStream.use { os ->
                os.write(dnsQueryBytes)
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                connection.inputStream.use { inputStream ->
                    return@withContext inputStream.readBytes()
                }
            }
            null
        } catch (e: Exception) {
            null
        } finally {
            connection?.disconnect()
        }
    }
}
