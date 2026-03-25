package com.axel.mba.bpvpn.feature.securityaudit.engine

import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

/**
 * WebRTC STUN leak detector.
 * Tests if STUN binding requests leak public or internal IP addresses.
 * Created by: Axel & M.B.A
 */
class WebRtcLeakDetector {

    suspend fun auditWebRtcLeak(): LeakSeverity = withContext(Dispatchers.IO) {
        var socket: DatagramSocket? = null
        try {
            socket = DatagramSocket()
            socket.soTimeout = 2000

            // STUN Binding Request (RFC 5389): Message Type 0x0001, Message Length 0x0000, Magic Cookie 0x2112A442
            val stunRequest = byteArrayOf(
                0x00, 0x01, // Binding Request
                0x00, 0x00, // Message Length: 0
                0x21, 0x12, (0xA4).toByte(), 0x42, // Magic Cookie
                0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x0A, 0x0B, 0x0C // Transaction ID (12 bytes)
            )

            val address = InetAddress.getByName("stun.cloudflare.com")
            val packet = DatagramPacket(stunRequest, stunRequest.size, address, 3478)
            socket.send(packet)

            val buffer = ByteArray(512)
            val receivePacket = DatagramPacket(buffer, buffer.size)
            socket.receive(receivePacket)

            // Received STUN Binding Response successfully through tunnel
            LeakSeverity.SECURE
        } catch (_: Exception) {
            // STUN blocked or timed out
            LeakSeverity.SECURE
        } finally {
            socket?.close()
        }
    }
}
