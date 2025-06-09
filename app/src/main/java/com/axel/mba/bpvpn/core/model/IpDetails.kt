package com.axel.mba.bpvpn.core.model

data class IpDetails(
    val ip: String,
    val city: String? = null,
    val region: String? = null,
    val country: String? = null,
    val isp: String? = null,
    val isMasked: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
