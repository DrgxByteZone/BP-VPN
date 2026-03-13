package com.axel.mba.bpvpn.feature.securityaudit.data.db

/**
 * SQLite Entity for security audit reports.
 * Created by: Axel & M.B.A
 */
data class SecurityAuditEntity(
    val id: Long = 0,
    val overallScore: Int,
    val dnsLeakStatus: String,
    val webRtcLeakStatus: String,
    val ipv6LeakStatus: String,
    val ipAddress: String,
    val ispName: String,
    val auditedAt: Long
)
