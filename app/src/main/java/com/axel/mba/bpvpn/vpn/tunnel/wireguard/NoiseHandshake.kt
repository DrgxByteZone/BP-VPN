package com.axel.mba.bpvpn.vpn.tunnel.wireguard

import java.nio.ByteBuffer

/**
 * Noise_IKpsk2 handshake packet builder for WireGuard.
 */
object NoiseHandshake {

    fun buildInitiationPacket(
        senderIndex: Int,
        ephemeralPublicKey: ByteArray,
        staticPublicKeyEncrypted: ByteArray,
        timestampEncrypted: ByteArray,
        mac1: ByteArray,
        mac2: ByteArray
    ): ByteBuffer {
        val buffer = ByteBuffer.allocate(CryptoConstants.HANDSHAKE_INITIATION_SIZE)
        buffer.put(1.toByte()) // Message type: 1 (Handshake Initiation)
        buffer.put(0.toByte()) // Reserved
        buffer.put(0.toByte())
        buffer.put(0.toByte())
        buffer.putInt(senderIndex)
        buffer.put(ephemeralPublicKey)
        buffer.put(staticPublicKeyEncrypted)
        buffer.put(timestampEncrypted)
        buffer.put(mac1)
        buffer.put(mac2)
        buffer.flip()
        return buffer
    }
}
