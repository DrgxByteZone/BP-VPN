package com.axel.mba.bpvpn.vpn.tunnel

import android.net.VpnService
import com.axel.mba.bpvpn.core.model.ProtectionMode
import com.axel.mba.bpvpn.core.model.WarpProfile
import com.axel.mba.bpvpn.core.network.packet.IpPacket
import com.axel.mba.bpvpn.vpn.tunnel.warp.WarpTunnelEngine
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

class TunnelManager(
    private val vpnService: VpnService,
    private val warpEngine: WarpTunnelEngine
) {

    private var currentMode: ProtectionMode = ProtectionMode.FULL_STEALTH

    fun setMode(mode: ProtectionMode) {
        currentMode = mode
    }

    suspend fun connectUpstream(profile: WarpProfile?) {
        if (currentMode == ProtectionMode.FULL_STEALTH && profile != null) {
            try {
                warpEngine.connect(profile)
            } catch (e: Exception) {
                // Fallback to local shield if upstream unreachable
                currentMode = ProtectionMode.LOCAL_SHIELD
            }
        }
    }

    fun forwardPacket(packet: IpPacket, buffer: ByteBuffer, outChannel: FileChannel) {
        if (currentMode == ProtectionMode.FULL_STEALTH && warpEngine.isConnected) {
            warpEngine.forwardPacket(packet, buffer, outChannel)
        } else {
            // Local Shield: Packets pass directly
        }
    }

    fun disconnect() {
        warpEngine.disconnect()
    }
}
