package com.axel.mba.bpvpn.feature.securityaudit.engine

import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.Inet6Address
import java.net.NetworkInterface

/**
 * Checks if IPv6 traffic leaks outside the VPN tunnel.
 * Created by: Axel & M.B.A
 */
class Ipv6LeakChecker {

    suspend fun auditIpv6(): LeakSeverity = withContext(Dispatchers.IO) {
        try {
            val interfaces = NetworkInterface.getNetworkInterfaces()
            var hasUnprotectedIpv6 = false

            while (interfaces.hasMoreElements()) {
                val nif = interfaces.nextElement()
                if (nif.isLoopback || !nif.isUp) continue

                // Check physical Wi-Fi or cellular interfaces
                if (!nif.name.startsWith("tun") && !nif.name.startsWith("wg")) {
                    val addrs = nif.inetAddresses
                    while (addrs.hasMoreElements()) {
                        val addr = addrs.nextElement()
                        if (addr is Inet6Address && !addr.isLinkLocalAddress && !addr.isLoopbackAddress) {
                            // Public or global IPv6 detected on physical interface
                            hasUnprotectedIpv6 = true
                            break
                        }
                    }
                }
            }

            if (hasUnprotectedIpv6) LeakSeverity.WARNING else LeakSeverity.SECURE
        } catch (_: Exception) {
            LeakSeverity.SECURE
        }
    }
}
