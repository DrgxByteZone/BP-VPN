package com.axel.mba.bpvpn.core.network.packet

import java.nio.ByteBuffer

object PacketParser {

    /**
     * Parses byte buffer into an [IpPacket] safely without memory allocation where possible.
     */
    fun parse(buffer: ByteBuffer): IpPacket? {
        if (!buffer.hasRemaining()) return null
        return IpPacket.parse(buffer)
    }
}
