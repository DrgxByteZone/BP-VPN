package com.axel.mba.bpvpn.vpn.tunnel.wireguard

import java.nio.ByteBuffer

object PacketEncapsulator {

    fun encapsulateDataPacket(
        receiverIndex: Int,
        counter: Long,
        encryptedPayload: ByteArray
    ): ByteBuffer {
        val totalSize = CryptoConstants.DATA_PACKET_HEADER_SIZE + encryptedPayload.size
        val buffer = ByteBuffer.allocate(totalSize)
        buffer.put(4.toByte()) // Message type: 4 (Transport Data)
        buffer.put(0.toByte()) // Reserved
        buffer.put(0.toByte())
        buffer.put(0.toByte())
        buffer.putInt(receiverIndex)
        buffer.putLong(counter)
        buffer.put(encryptedPayload)
        buffer.flip()
        return buffer
    }
}
