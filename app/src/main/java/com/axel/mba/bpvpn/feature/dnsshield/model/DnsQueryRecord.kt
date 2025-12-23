package com.axel.mba.bpvpn.feature.dnsshield.model

/**
 * Log record of an encrypted DNS query.
 * Created by: Axel & M.B.A
 */
data class DnsQueryRecord(
    val domain: String,
    val resolvedIp: String,
    val responseTimeMs: Long,
    val isBlocked: Boolean,
    val blockReason: String? = null,
    val timestamp: Long = System.currentTimeMillis()
)
