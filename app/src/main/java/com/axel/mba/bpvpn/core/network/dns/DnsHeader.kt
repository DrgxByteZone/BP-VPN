package com.axel.mba.bpvpn.core.network.dns

import java.nio.ByteBuffer

/**
 * DNS Header according to RFC 1035 (12 bytes).
 */
data class DnsHeader(
    val transactionId: Int,
    val flags: Int,
    val questionCount: Int,
    val answerCount: Int,
    val authorityCount: Int,
    val additionalCount: Int
) {
    val isQuery: Boolean get() = (flags and 0x8000) == 0
    val isResponse: Boolean get() = (flags and 0x8000) != 0
    val opcode: Int get() = (flags shr 11) and 0x0F
    val rcode: Int get() = flags and 0x0F

    companion object {
        const val HEADER_LENGTH = 12

        fun parse(buffer: ByteBuffer, offset: Int): DnsHeader? {
            if (buffer.limit() - offset < HEADER_LENGTH) return null

            val id = buffer.getShort(offset).toInt() and 0xFFFF
            val flags = buffer.getShort(offset + 2).toInt() and 0xFFFF
            val qdCount = buffer.getShort(offset + 4).toInt() and 0xFFFF
            val anCount = buffer.getShort(offset + 6).toInt() and 0xFFFF
            val nsCount = buffer.getShort(offset + 8).toInt() and 0xFFFF
            val arCount = buffer.getShort(offset + 10).toInt() and 0xFFFF

            return DnsHeader(
                transactionId = id,
                flags = flags,
                questionCount = qdCount,
                answerCount = anCount,
                authorityCount = nsCount,
                additionalCount = arCount
            )
        }
    }
}
