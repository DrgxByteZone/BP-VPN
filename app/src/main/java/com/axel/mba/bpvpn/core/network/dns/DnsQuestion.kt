package com.axel.mba.bpvpn.core.network.dns

import java.nio.ByteBuffer

/**
 * DNS Question field (QName, QType, QClass).
 */
data class DnsQuestion(
    val domain: String,
    val qType: Int,
    val qClass: Int
) {
    val isTypeA: Boolean get() = qType == 1
    val isTypeAaaa: Boolean get() = qType == 28

    companion object {
        fun parse(buffer: ByteBuffer, startOffset: Int, outEndPos: IntArray): DnsQuestion? {
            var pos = startOffset
            val domainBuilder = StringBuilder()

            while (pos < buffer.limit()) {
                val len = buffer.get(pos).toInt() and 0xFF
                if (len == 0) {
                    pos++
                    break
                }
                // DNS name compression pointer (RFC 1035 section 4.1.4)
                if ((len and 0xC0) == 0xC0) {
                    pos += 2
                    break
                }
                pos++
                if (domainBuilder.isNotEmpty()) domainBuilder.append('.')
                for (i in 0 until len) {
                    if (pos < buffer.limit()) {
                        domainBuilder.append(buffer.get(pos).toInt().toChar())
                        pos++
                    }
                }
            }

            if (buffer.limit() - pos < 4) return null
            val qType = buffer.getShort(pos).toInt() and 0xFFFF
            val qClass = buffer.getShort(pos + 2).toInt() and 0xFFFF
            outEndPos[0] = pos + 4

            return DnsQuestion(
                domain = domainBuilder.toString().lowercase(),
                qType = qType,
                qClass = qClass
            )
        }
    }
}
