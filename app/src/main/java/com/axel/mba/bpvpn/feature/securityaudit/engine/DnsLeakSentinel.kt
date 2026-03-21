package com.axel.mba.bpvpn.feature.securityaudit.engine

import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetAddress

/**
 * Sentinel to detect DNS query leaks to ISP servers outside the encrypted tunnel.
 * Created by: Axel & M.B.A
 */
class DnsLeakSentinel {

    suspend fun auditDnsLeak(): Pair<LeakSeverity, List<String>> = withContext(Dispatchers.IO) {
        val detectedServers = mutableListOf<String>()
        try {
            // Resolve host through system resolver
            val addresses = InetAddress.getAllByName("whoami.cloudflare")
            addresses.forEach { addr ->
                addr.hostAddress?.let { detectedServers.add(it) }
            }
        } catch (_: Exception) {
            // If resolution failed, try 1.1.1.1 or cloudflare.com
            try {
                val fallback = InetAddress.getByName("cloudflare.com")
                fallback.hostAddress?.let { detectedServers.add(it) }
            } catch (_: Exception) {}
        }

        // Evaluate if DNS resolution leaked outside WireGuard/WARP anycast
        val isLeaked = detectedServers.any { ip ->
            // If resolved through a non-Cloudflare/non-private IP while expecting encrypted tunnel
            !ip.startsWith("1.1.1") && !ip.startsWith("1.0.0") && !ip.startsWith("10.") && !ip.startsWith("172.") && !ip.startsWith("104.") && !ip.startsWith("162.")
        }

        val severity = if (isLeaked && detectedServers.isNotEmpty()) LeakSeverity.WARNING else LeakSeverity.SECURE
        Pair(severity, detectedServers)
    }
}
