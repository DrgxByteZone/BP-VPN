package com.axel.mba.bpvpn.core.network.packet

import java.nio.ByteBuffer

/**
 * Parses TCP header from raw byte buffer (RFC 793).
 */
data class TcpHeader(
    val sourcePort: Int,
    val destinationPort: Int,
    val sequenceNumber: Long,
    val acknowledgmentNumber: Long,
    val dataOffset: Int,
    val flags: Int
) {
    val isSyn: Boolean get() = (flags and 0x02) != 0
    val isAck: Boolean get() = (flags and 0x10) != 0
    val isFin: Boolean get() = (flags and 0x01) != 0
    val isRst: Boolean get() = (flags and 0x04) != 0

    companion object {
        fun parse(buffer: ByteBuffer, offset: Int): TcpHeader? {
            if (buffer.limit() - offset < 20) return null

            val srcPort = buffer.getShort(offset).toInt() and 0xFFFF
            val dstPort = buffer.getShort(offset + 2).toInt() and 0xFFFF
            val seq = buffer.getInt(offset + 4).toLong() and 0xFFFFFFFFL
            val ack = buffer.getInt(offset + 8).toLong() and 0xFFFFFFFFL
            val dataOffsetAndFlags = buffer.getShort(offset + 12).toInt() and 0xFFFF
            val dataOffset = ((dataOffsetAndFlags shr 12) and 0x0F) * 4
            val flags = dataOffsetAndFlags and 0x01FF

            return TcpHeader(
                sourcePort = srcPort,
                destinationPort = dstPort,
                sequenceNumber = seq,
                acknowledgmentNumber = ack,
                dataOffset = dataOffset,
                flags = flags
            )
        }
    }
}
