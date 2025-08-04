package com.axel.mba.bpvpn.core.network.dns

import android.net.VpnService
import com.axel.mba.bpvpn.core.network.packet.IpPacket
import com.axel.mba.bpvpn.core.network.packet.PacketChecksum
import com.axel.mba.bpvpn.core.network.packet.UdpHeader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetSocketAddress
import java.nio.ByteBuffer
import java.nio.channels.FileChannel

/**
 * High-speed Hybrid DNS Forwarder.
 * Uses Encrypted DNS-over-HTTPS (DoH) as primary to completely bypass ISP DNS blocks / hijacking,
 * with fallbacks to Google DoH and protected UDP sockets.
 * Asynchronously delivers responses back into the TUN interface without blocking the packet pump.
 */
class DnsForwarder(
    private val vpnService: VpnService
) {

    private val cloudflareDoH = DoHClient("https://cloudflare-dns.com/dns-query")
    private val googleDoH = DoHClient("https://dns.google/dns-query")

    private val upstreamResolvers = listOf(
        InetSocketAddress("1.1.1.1", 53),
        InetSocketAddress("8.8.8.8", 53)
    )

    private var socket: DatagramSocket? = null

    init {
        initSocket()
    }

    private fun initSocket() {
        try {
            val s = DatagramSocket()
            if (vpnService.protect(s)) {
                s.soTimeout = 2000
                socket = s
            } else {
                s.close()
            }
        } catch (e: Exception) {
            socket = null
        }
    }

    fun forwardDnsQuery(
        originalPacket: IpPacket,
        originalBuffer: ByteBuffer,
        outChannel: FileChannel,
        scope: CoroutineScope
    ) {
        val dnsOffset = originalPacket.payloadOffset
        val dnsLength = originalPacket.payloadLength
        if (dnsLength <= 0 || dnsLength > 1500) return

        val queryBytes = ByteArray(dnsLength)
        val oldPos = originalBuffer.position()
        originalBuffer.position(dnsOffset)
        originalBuffer.get(queryBytes)
        originalBuffer.position(oldPos)

        // Asynchronous resolution so PacketPump read loop is NEVER blocked
        scope.launch(Dispatchers.IO) {
            try {
                // 1. Try Cloudflare DoH (Port 443 HTTPS - cannot be blocked by Indonesian ISPs)
                var responseBytes = cloudflareDoH.resolve(queryBytes)

                // 2. Try Google DoH fallback
                if (responseBytes == null || responseBytes.isEmpty()) {
                    responseBytes = googleDoH.resolve(queryBytes)
                }

                // 3. Fallback to protected UDP socket
                if (responseBytes == null || responseBytes.isEmpty()) {
                    responseBytes = resolveViaUdp(queryBytes)
                }

                if (responseBytes != null && responseBytes.isNotEmpty()) {
                    writeResponsePacket(originalPacket, responseBytes, outChannel)
                }
            } catch (e: Exception) {
                // Ignore resolution error
            }
        }
    }

    private fun resolveViaUdp(queryBytes: ByteArray): ByteArray? {
        val s = socket ?: run {
            initSocket()
            socket ?: return null
        }
        return try {
            val targetResolver = upstreamResolvers[0]
            val sendPacket = DatagramPacket(
                queryBytes,
                queryBytes.size,
                targetResolver.address,
                targetResolver.port
            )
            s.send(sendPacket)

            val responseBuffer = ByteArray(2048)
            val receivePacket = DatagramPacket(responseBuffer, responseBuffer.size)
            s.receive(receivePacket)

            if (receivePacket.length > 0) {
                responseBuffer.copyOf(receivePacket.length)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    private fun writeResponsePacket(
        originalPacket: IpPacket,
        responseBytes: ByteArray,
        outChannel: FileChannel
    ) {
        synchronized(outChannel) {
            val totalUdpLength = UdpHeader.UDP_HEADER_LENGTH + responseBytes.size
            val totalIpLength = 20 + totalUdpLength

            val outBuf = ByteBuffer.allocate(totalIpLength)

            // 1. IPv4 Header (20 bytes)
            outBuf.put(0x45.toByte()) // Version 4, IHL 5
            outBuf.put(0x00.toByte())
            outBuf.putShort(totalIpLength.toShort())
            outBuf.putShort((Math.random() * 65535).toInt().toShort())
            outBuf.putShort(0x4000.toShort()) // Flags: Don't Fragment
            outBuf.put(64.toByte()) // TTL
            outBuf.put(17.toByte()) // Protocol: UDP
            outBuf.putShort(0.toShort()) // Checksum placeholder

            // Swap Source & Destination IP
            val srcIp = originalPacket.ipHeader.destinationIp.address
            val dstIp = originalPacket.ipHeader.sourceIp.address
            outBuf.put(srcIp)
            outBuf.put(dstIp)

            // Compute IPv4 checksum
            val ipChecksum = PacketChecksum.computeChecksum(outBuf, 0, 20)
            outBuf.putShort(10, ipChecksum.toShort())

            // 2. UDP Header (8 bytes)
            val srcPort = originalPacket.udpHeader?.destinationPort ?: 53
            val dstPort = originalPacket.udpHeader?.sourcePort ?: 53
            outBuf.putShort(srcPort.toShort())
            outBuf.putShort(dstPort.toShort())
            outBuf.putShort(totalUdpLength.toShort())
            outBuf.putShort(0.toShort()) // Checksum 0 is valid for IPv4 UDP

            // 3. DNS Payload
            outBuf.put(responseBytes)

            outBuf.flip()
            try {
                outChannel.write(outBuf)
            } catch (e: Exception) {
                // Channel closed
            }
        }
    }

    fun close() {
        try {
            socket?.close()
        } catch (e: Exception) {
            // Ignore
        } finally {
            socket = null
        }
    }
}
