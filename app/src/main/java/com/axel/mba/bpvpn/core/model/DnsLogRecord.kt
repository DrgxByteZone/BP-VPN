package com.axel.mba.bpvpn.core.model

data class DnsLogRecord(
    val id: Long = 0,
    val domain: String,
    val queryType: String = "A",
    val resolvedIp: String,
    val isBlocked: Boolean,
    val threatCategory: ThreatCategory? = null,
    val requestingAppPackage: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
