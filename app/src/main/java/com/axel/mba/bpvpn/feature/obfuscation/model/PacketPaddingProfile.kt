package com.axel.mba.bpvpn.feature.obfuscation.model

/**
 * Random non-uniform packet padding specification.
 * Created by: Axel & M.B.A
 */
data class PacketPaddingProfile(
    val minPaddingBytes: Int = 16,
    val maxPaddingBytes: Int = 64,
    val useCryptographicNoise: Boolean = true
)
