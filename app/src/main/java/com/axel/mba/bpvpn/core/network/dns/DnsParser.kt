package com.axel.mba.bpvpn.core.network.dns

import java.nio.ByteBuffer

object DnsParser {

    fun parse(buffer: ByteBuffer, offset: Int): DnsPacket? {
        val header = DnsHeader.parse(buffer, offset) ?: return null
        val questions = mutableListOf<DnsQuestion>()

        var currentOffset = offset + DnsHeader.HEADER_LENGTH
        val endPos = IntArray(1)

        for (i in 0 until header.questionCount) {
            val q = DnsQuestion.parse(buffer, currentOffset, endPos) ?: break
            questions.add(q)
            currentOffset = endPos[0]
        }

        return DnsPacket(header = header, questions = questions)
    }
}
