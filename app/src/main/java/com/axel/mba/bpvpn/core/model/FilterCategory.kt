package com.axel.mba.bpvpn.core.model

data class FilterCategory(
    val id: String,
    val name: String,
    val description: String,
    val isEnabled: Boolean = true,
    val ruleCount: Int = 0
)

data class WarpProfile(
    val accountId: String,
    val accessToken: String,
    val privateKey: String,
    val publicKey: String,
    val clientIpv4: String,
    val clientIpv6: String?,
    val peerPublicKey: String,
    val endpointHost: String,
    val endpointPort: Int
)
