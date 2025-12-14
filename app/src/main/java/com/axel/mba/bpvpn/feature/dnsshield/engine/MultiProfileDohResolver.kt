package com.axel.mba.bpvpn.feature.dnsshield.engine

import com.axel.mba.bpvpn.feature.dnsshield.model.DnsProfile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

/**
 * Multi-Profile DNS-over-HTTPS (DoH) encrypted resolver.
 * Created by: Axel & M.B.A
 */
class MultiProfileDohResolver(
    private val cacheManager: DnsCacheManager = DnsCacheManager()
) {

    suspend fun resolve(
        domain: String,
        profile: DnsProfile = DnsProfile.CLOUDFLARE_STANDARD,
        timeoutMs: Int = 3000
    ): List<String> = withContext(Dispatchers.IO) {
        // 1. Check RAM Cache
        cacheManager.get(domain)?.let { cachedIps ->
            return@withContext cachedIps
        }

        // 2. Perform DoH Wire-Format POST
        var connection: HttpURLConnection? = null
        var inputStream: InputStream? = null

        try {
            val queryBytes = DohPacketSerializer.buildDnsQuery(domain)
            val url = URL(profile.dohEndpoint)

            connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                connectTimeout = timeoutMs
                readTimeout = timeoutMs
                doOutput = true
                setRequestProperty("Content-Type", "application/dns-message")
                setRequestProperty("Accept", "application/dns-message")
                setRequestProperty("User-Agent", "BPVPN-DohResolver/1.0")
            }

            connection.outputStream.use { os ->
                os.write(queryBytes)
                os.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode == 200) {
                inputStream = connection.inputStream
                val baos = ByteArrayOutputStream()
                val buffer = ByteArray(2048)
                var read: Int
                while (inputStream.read(buffer).also { read = it } != -1) {
                    baos.write(buffer, 0, read)
                }

                val parsedIps = DohPacketSerializer.parseIpsFromResponse(baos.toByteArray())
                if (parsedIps.isNotEmpty()) {
                    cacheManager.put(domain, parsedIps)
                    return@withContext parsedIps
                }
            }
        } catch (_: Exception) {
            // DoH request failed or timed out
        } finally {
            try { inputStream?.close() } catch (_: Exception) {}
            try { connection?.disconnect() } catch (_: Exception) {}
        }

        // Fallback to Primary Profile IP
        listOf(profile.primaryIp)
    }

    fun getCacheSize(): Int = cacheManager.size()
    fun clearCache() = cacheManager.clear()
}
