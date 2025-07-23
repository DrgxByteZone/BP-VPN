package com.axel.mba.bpvpn.core.network.packet

import java.nio.ByteBuffer

/**
 * Encapsulates a complete raw IP packet, its header, and underlying transport layer.
 */
class IpPacket private constructor(
    val ipHeader: IpHeader,
    val udpHeader: UdpHeader?,
    val tcpHeader: TcpHeader?,
    val rawBuffer: ByteBuffer,
    val payloadOffset: Int,
    val payloadLength: Int
) {
    val isDnsQuery: Boolean get() = udpHeader?.isDns == true

    companion object {
        fun parse(buffer: ByteBuffer): IpPacket? {
            val ipHeader = IpHeader.parse(buffer) ?: return null
            val ipHeaderLen = ipHeader.headerLength

            var udpHeader: UdpHeader? = null
            var tcpHeader: TcpHeader? = null
            var payloadOffset = buffer.position() + ipHeaderLen
            var payloadLength = (ipHeader.totalLength - ipHeaderLen).coerceAtLeast(0)

            if (ipHeader.isUdp) {
                udpHeader = UdpHeader.parse(buffer, payloadOffset)
                if (udpHeader != null) {
                    payloadOffset += UdpHeader.UDP_HEADER_LENGTH
                    payloadLength = (udpHeader.length - UdpHeader.UDP_HEADER_LENGTH).coerceAtLeast(0)
                }
            } else if (ipHeader.isTcp) {
                tcpHeader = TcpHeader.parse(buffer, payloadOffset)
                if (tcpHeader != null) {
                    payloadOffset += tcpHeader.dataOffset
                    payloadLength = (ipHeader.totalLength - ipHeaderLen - tcpHeader.dataOffset).coerceAtLeast(0)
                }
            }

            return IpPacket(
                ipHeader = ipHeader,
                udpHeader = udpHeader,
                tcpHeader = tcpHeader,
                rawBuffer = buffer,
                payloadOffset = payloadOffset,
                payloadLength = payloadLength
            )
        }
    }
}
