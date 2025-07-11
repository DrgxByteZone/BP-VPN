package com.axel.mba.bpvpn.vpn.tunnel.warp

import android.net.VpnService
import com.axel.mba.bpvpn.core.model.WarpProfile
import com.axel.mba.bpvpn.core.network.packet.IpPacket
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * Tunnel engine that forwards clean packets to Cloudflare Anycast Edge for IP Masking.
 */
class WarpTunnelEngine(
    private val vpnService: VpnService
) {

    private var udpSocket: DatagramSocket? = null
    private var activeProfile: WarpProfile? = null

    suspend fun connect(profile: WarpProfile) = withContext(Dispatchers.IO) {
        disconnect()
        activeProfile = profile

        val socket = DatagramSocket()
        // Crucial: Protect socket from being looped back into the VPN TUN interface
        val protected = vpnService.protect(socket)
        if (!protected) {
            socket.close()
            throw IOException("VpnService failed to protect upstream tunnel UDP socket")
        }

        socket.connect(InetSocketAddress(profile.endpointHost, profile.endpointPort))
        udpSocket = socket
    }

    fun forwardPacket(packet: IpPacket, buffer: ByteBuffer, outChannel: FileChannel) {
        val socket = udpSocket ?: return
        try {
            val length = buffer.remaining()
            val bytes = ByteArray(length)
            val oldPos = buffer.position()
            buffer.get(bytes)
            buffer.position(oldPos)

            val dPacket = DatagramPacket(bytes, length)
            socket.send(dPacket)
        } catch (e: Exception) {
            // Socket error or connection interrupted
        }
    }

    fun disconnect() {
        try {
            udpSocket?.close()
        } catch (e: Exception) {
            // Ignore
        } finally {
            udpSocket = null
            activeProfile = null
        }
    }

    val isConnected: Boolean get() = udpSocket?.isConnected == true
}
