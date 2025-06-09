package com.axel.mba.bpvpn.core.model

enum class ProtectionMode(val displayName: String, val description: String) {
    FULL_STEALTH(
        displayName = "Full Stealth",
        description = "On-Device DNS Sinkhole + Encrypted WireGuard Egress IP Masking"
    ),
    LOCAL_SHIELD(
        displayName = "Local Shield",
        description = "Ultra-Fast On-Device DNS Sinkhole & App Firewall (Native Speed)"
    )
}
