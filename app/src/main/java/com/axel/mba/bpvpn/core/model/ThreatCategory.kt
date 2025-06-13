package com.axel.mba.bpvpn.core.model

enum class ThreatCategory(val label: String) {
    ADVERTISING("Ad / Banner"),
    TRACKER("Telemetry / Tracker"),
    MALWARE("Malware / Scam"),
    SOCIAL_TRACKER("Social Network Telemetry"),
    CUSTOM_RULE("User Custom Blocklist")
}
