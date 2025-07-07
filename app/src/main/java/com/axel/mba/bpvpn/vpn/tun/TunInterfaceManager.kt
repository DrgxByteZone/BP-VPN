package com.axel.mba.bpvpn.vpn.tun

import android.net.VpnService
import android.os.ParcelFileDescriptor
import com.axel.mba.bpvpn.core.common.Constants
import java.io.IOException

class TunInterfaceManager(private val vpnService: VpnService) {

    private var pfd: ParcelFileDescriptor? = null

    fun establish(
        disallowedApps: Set<String> = emptySet(),
        allowedApps: Set<String> = emptySet()
    ): ParcelFileDescriptor {
        close()

        val builder = vpnService.Builder().apply {
            setSession(Constants.APP_FULL_NAME)
            setMtu(Constants.VPN_MTU)
            // Assign Virtual Interface IP
            addAddress("10.240.0.2", 32)

            // Direct system DNS to our virtual sinkhole IP
            addDnsServer("10.240.0.1")

            // Intercept DNS queries via our virtual sinkhole IP
            addRoute("10.240.0.1", 32)

            // Per-App Firewall inclusions/exclusions
            if (allowedApps.isNotEmpty()) {
                for (pkg in allowedApps) {
                    try {
                        addAllowedApplication(pkg)
                    } catch (e: Exception) {
                        // Package uninstalled or invalid
                    }
                }
            } else {
                for (pkg in disallowedApps) {
                    try {
                        addDisallowedApplication(pkg)
                    } catch (e: Exception) {
                        // Package uninstalled or invalid
                    }
                }
            }
        }

        val descriptor = builder.establish()
            ?: throw IOException("Failed to establish VpnService TUN interface (permission missing or system error)")

        pfd = descriptor
        return descriptor
    }

    fun close() {
        try {
            pfd?.close()
        } catch (e: Exception) {
            // Ignore on shutdown
        } finally {
            pfd = null
        }
    }

    val isEstablished: Boolean get() = pfd != null
}
