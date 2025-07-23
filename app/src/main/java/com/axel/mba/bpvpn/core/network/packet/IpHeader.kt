package com.axel.mba.bpvpn.core.network.packet

import java.net.InetAddress
import java.nio.ByteBuffer

/**
 * Parses IPv4 & IPv6 headers from raw byte buffer (RFC 791 / RFC 8200).
 */
data class IpHeader(
    val version: Int,
    val headerLength: Int,
    val totalLength: Int,
    val protocol: Int, // 17 for UDP, 6 for TCP, 1 for ICMP
    val sourceIp: InetAddress,
    val destinationIp: InetAddress
) {
    val isIpv4: Boolean get() = version == 4
    val isIpv6: Boolean get() = version == 6
    val isUdp: Boolean get() = protocol == 17
    val isTcp: Boolean get() = protocol == 6

    companion object {
        fun parse(buffer: ByteBuffer): IpHeader? {
            if (buffer.remaining() < 20) return null
            val startPos = buffer.position()

            val versionAndIhl = buffer.get(startPos).toInt() and 0xFF
            val version = versionAndIhl shr 4

            if (version == 4) {
                val ihl = (versionAndIhl and 0x0F) * 4
                if (buffer.remaining() < ihl) return null

                val totalLength = buffer.getShort(startPos + 2).toInt() and 0xFFFF
                val protocol = buffer.get(startPos + 9).toInt() and 0xFF

                val srcBytes = ByteArray(4)
                val dstBytes = ByteArray(4)

                val oldPos = buffer.position()
                buffer.position(startPos + 12)
                buffer.get(srcBytes)
                buffer.get(dstBytes)
                buffer.position(oldPos)

                return IpHeader(
                    version = 4,
                    headerLength = ihl,
                    totalLength = totalLength,
                    protocol = protocol,
                    sourceIp = InetAddress.getByAddress(srcBytes),
                    destinationIp = InetAddress.getByAddress(dstBytes)
                )
            } else if (version == 6) {
                if (buffer.remaining() < 40) return null
                val payloadLength = buffer.getShort(startPos + 4).toInt() and 0xFFFF
                val nextHeader = buffer.get(startPos + 6).toInt() and 0xFF

                val srcBytes = ByteArray(16)
                val dstBytes = ByteArray(16)

                val oldPos = buffer.position()
                buffer.position(startPos + 8)
                buffer.get(srcBytes)
                buffer.get(dstBytes)
                buffer.position(oldPos)

                return IpHeader(
                    version = 6,
                    headerLength = 40,
                    totalLength = 40 + payloadLength,
                    protocol = nextHeader,
                    sourceIp = InetAddress.getByAddress(srcBytes),
                    destinationIp = InetAddress.getByAddress(dstBytes)
                )
            }
            return null
        }
    }
}
