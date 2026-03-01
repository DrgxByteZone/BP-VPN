package com.axel.mba.bpvpn.feature.obfuscation.engine

import com.axel.mba.bpvpn.feature.obfuscation.model.PacketPaddingProfile
import java.security.SecureRandom

/**
 * Generates cryptographic noise and random padding bytes for handshake frames.
 * Created by: Axel & M.B.A
 */
class PacketPaddingGenerator(private val profile: PacketPaddingProfile = PacketPaddingProfile()) {

    private val secureRandom = SecureRandom()

    fun generatePadding(): ByteArray {
        val range = (profile.maxPaddingBytes - profile.minPaddingBytes).coerceAtLeast(1)
        val length = profile.minPaddingBytes + secureRandom.nextInt(range)
        val padding = ByteArray(length)
        secureRandom.nextBytes(padding)
        return padding
    }
}
