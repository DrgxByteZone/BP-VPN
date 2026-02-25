package com.axel.mba.bpvpn.feature.obfuscation.engine

import com.axel.mba.bpvpn.feature.obfuscation.model.MssClampingConfig

/**
 * Path MTU & MSS clamping calculator.
 * Ensures packets never exceed mobile carrier MTU (avoids TCP packet drop).
 * Created by: Axel & M.B.A
 */
object MtuOptimizer {

    const val STANDARD_ETHERNET_MTU = 1500
    const val SAFE_WIREGUARD_MTU = 1280
    const val MINIMUM_IPV6_MTU = 1280

    fun calculateOptimalConfig(isCellular: Boolean): MssClampingConfig {
        val targetMtu = if (isCellular) SAFE_WIREGUARD_MTU else 1420
        val mss = targetMtu - 40 // Standard TCP + IP header overhead
        return MssClampingConfig(
            isAutoClamping = true,
            targetMtu = targetMtu,
            calculatedMss = mss
        )
    }
}
