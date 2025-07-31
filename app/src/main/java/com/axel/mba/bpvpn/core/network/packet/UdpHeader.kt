package com.axel.mba.bpvpn.core.network.packet

import java.nio.ByteBuffer

/**
 * Parses UDP header from raw byte buffer (RFC 768).
 */
data class UdpHeader(
    val sourcePort: Int,
    val destinationPort: Int,
    val length: Int,
    val checksum: Int
) {
    val isDns: Boolean get() = sourcePort == 53 || destinationPort == 53

    companion object {
        const val UDP_HEADER_LENGTH = 8

        fun parse(buffer: ByteBuffer, offset: Int): UdpHeader? {
            if (buffer.limit() - offset < UDP_HEADER_LENGTH) return null

            val srcPort = buffer.getShort(offset).toInt() and 0xFFFF
            val dstPort = buffer.getShort(offset + 2).toInt() and 0xFFFF
            val length = buffer.getShort(offset + 4).toInt() and 0xFFFF
            val checksum = buffer.getShort(offset + 6).toInt() and 0xFFFF

            return UdpHeader(
                sourcePort = srcPort,
                destinationPort = dstPort,
                length = length,
                checksum = checksum
            )
        }
    }
}
