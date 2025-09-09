package com.axel.mba.bpvpn.data.remote.model

data class IpResponseDto(
    val ip: String,
    val city: String? = null,
    val region: String? = null,
    val country: String? = null,
    val org: String? = null
)

data class WarpRegistrationDto(
    val id: String,
    val token: String,
    val account: WarpAccountDto?,
    val config: WarpConfigDto?
)

data class WarpAccountDto(
    val id: String,
    val accountType: String?
)

data class WarpConfigDto(
    val clientId: String?,
    val peers: List<WarpPeerDto>?
)

data class WarpPeerDto(
    val publicKey: String,
    val endpoint: WarpEndpointDto?
)

data class WarpEndpointDto(
    val host: String,
    val v4: String?,
    val v6: String?
)
