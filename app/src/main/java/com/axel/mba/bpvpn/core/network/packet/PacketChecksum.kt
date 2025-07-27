package com.axel.mba.bpvpn.core.network.packet

import java.nio.ByteBuffer

/**
 * Computes Internet Checksum according to RFC 1071.
 */
object PacketChecksum {

    fun computeChecksum(buffer: ByteBuffer, offset: Int, length: Int): Int {
        var sum = 0L
        var i = offset
        val end = offset + length

        while (i < end - 1) {
            val word = buffer.getShort(i).toInt() and 0xFFFF
            sum += word
            i += 2
        }

        if (i < end) {
            val lastByte = buffer.get(i).toInt() and 0xFF
            sum += (lastByte shl 8)
        }

        while ((sum shr 16) > 0) {
            sum = (sum and 0xFFFF) + (sum shr 16)
        }

        return (sum.inv().toInt()) and 0xFFFF
    }

    fun computeUdpPseudoHeaderChecksum(
        srcIp: ByteArray,
        dstIp: ByteArray,
        protocol: Int,
        udpLength: Int
    ): Long {
        var sum = 0L
        for (i in 0 until srcIp.size step 2) {
            val word = ((srcIp[i].toInt() and 0xFF) shl 8) or (srcIp[i + 1].toInt() and 0xFF)
            sum += word
        }
        for (i in 0 until dstIp.size step 2) {
            val word = ((dstIp[i].toInt() and 0xFF) shl 8) or (dstIp[i + 1].toInt() and 0xFF)
            sum += word
        }
        sum += protocol
        sum += udpLength
        return sum
    }
}
