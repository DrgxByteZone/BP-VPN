package com.axel.mba.bpvpn.core.network.dns

import com.axel.mba.bpvpn.core.common.Constants
import com.axel.mba.bpvpn.core.network.packet.IpPacket
import com.axel.mba.bpvpn.core.network.packet.PacketChecksum
import com.axel.mba.bpvpn.core.network.packet.UdpHeader
import java.nio.ByteBuffer

/**
 * High-performance binary DNS response synthesizer.
 * Constructs an RFC-compliant 0.0.0.0 A-Record reply directly into a ByteBuffer
 * with swapped IP/UDP headers and recalculated checksums.
 */
object DnsResponseBuilder {

    fun buildBlockedResponse(originalPacket: IpPacket, originalBuffer: ByteBuffer): ByteBuffer {
        val originalDnsOffset = originalPacket.payloadOffset
        val originalDnsLength = originalPacket.payloadLength

        // 12 bytes DNS Header + Question section + 16 bytes for Answer Record
        val questionLength = originalDnsLength - DnsHeader.HEADER_LENGTH
        val answerLength = 16 // 2 (name ptr) + 2 (type) + 2 (class) + 4 (ttl) + 2 (rdlength) + 4 (0.0.0.0)
        val newDnsLength = originalDnsLength + answerLength
        val newUdpLength = UdpHeader.UDP_HEADER_LENGTH + newDnsLength
        val newTotalLength = 20 + newUdpLength // 20 bytes IPv4 header

        val out = ByteBuffer.allocate(newTotalLength)

        // 1. Synthesize IPv4 Header (20 bytes)
        out.put(0x45.toByte()) // Version 4, IHL 5
        out.put(0x00.toByte()) // DSCP / ECN
        out.putShort(newTotalLength.toShort()) // Total length
        out.putShort(0x1234.toShort()) // Identification
        out.putShort(0x4000.toShort()) // Flags: Don't Fragment
        out.put(64.toByte()) // TTL
        out.put(17.toByte()) // Protocol: UDP
        out.putShort(0.toShort()) // Checksum placeholder

        // Swap Source & Destination IP
        val srcIp = originalPacket.ipHeader.destinationIp.address
        val dstIp = originalPacket.ipHeader.sourceIp.address
        out.put(srcIp)
        out.put(dstIp)

        // Compute and inject IPv4 header checksum
        val ipChecksum = PacketChecksum.computeChecksum(out, 0, 20)
        out.putShort(10, ipChecksum.toShort())

        // 2. Synthesize UDP Header (8 bytes)
        val srcPort = originalPacket.udpHeader?.destinationPort ?: 53
        val dstPort = originalPacket.udpHeader?.sourcePort ?: 53
        val udpStartPos = out.position()
        out.putShort(srcPort.toShort())
        out.putShort(dstPort.toShort())
        out.putShort(newUdpLength.toShort())
        out.putShort(0.toShort()) // UDP Checksum (0 is valid in IPv4)

        // 3. Synthesize DNS Header
        val originalId = originalBuffer.getShort(originalDnsOffset)
        out.putShort(originalId) // Same Transaction ID
        // Flags: Standard query response, No error, Authoritative
        out.putShort(0x8180.toShort())
        out.putShort(1.toShort()) // QDCOUNT: 1
        out.putShort(1.toShort()) // ANCOUNT: 1
        out.putShort(0.toShort()) // NSCOUNT: 0
        out.putShort(0.toShort()) // ARCOUNT: 0

        // 4. Copy original Question Section
        val originalQuestionStart = originalDnsOffset + DnsHeader.HEADER_LENGTH
        val oldPos = originalBuffer.position()
        originalBuffer.position(originalQuestionStart)
        val questionBytes = ByteArray(questionLength)
        originalBuffer.get(questionBytes)
        originalBuffer.position(oldPos)
        out.put(questionBytes)

        // 5. Append Synthetic Answer Record (0.0.0.0, TTL 300)
        out.putShort(0xC00C.toShort()) // Name pointer to question at offset 12
        out.putShort(1.toShort())      // Type A
        out.putShort(1.toShort())      // Class IN
        out.putInt(300)                // TTL 300 seconds
        out.putShort(4.toShort())      // RDLENGTH: 4 bytes
        out.put(0.toByte())            // 0.0.0.0
        out.put(0.toByte())
        out.put(0.toByte())
        out.put(0.toByte())

        out.flip()
        return out
    }
}
