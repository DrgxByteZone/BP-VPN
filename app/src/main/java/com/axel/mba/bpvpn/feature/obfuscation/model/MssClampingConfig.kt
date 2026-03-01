package com.axel.mba.bpvpn.feature.obfuscation.model

/**
 * Maximum Segment Size (MSS) clamping configuration to eliminate packet fragmentation.
 * Created by: Axel & M.B.A
 */
data class MssClampingConfig(
    val isAutoClamping: Boolean = true,
    val targetMtu: Int = 1280, // Safe MTU for IPv6 + WireGuard headers
    val calculatedMss: Int = 1240 // 1280 - 40 (TCP/IP overhead)
)
