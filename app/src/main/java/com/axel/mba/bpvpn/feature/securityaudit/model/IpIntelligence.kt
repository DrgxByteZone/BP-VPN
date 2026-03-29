package com.axel.mba.bpvpn.feature.securityaudit.model

/**
 * Detailed IP Intelligence and ASN metadata.
 * Created by: Axel & M.B.A
 */
data class IpIntelligence(
    val ip: String = "",
    val asn: String = "",
    val asOrganization: String = "",
    val country: String = "",
    val city: String = "",
    val isVpnDetected: Boolean = false,
    val threatScore: Int = 0 // 0 (clean) - 100 (high risk)
)
