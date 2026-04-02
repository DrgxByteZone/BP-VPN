package com.axel.mba.bpvpn.feature.securityaudit.model

/**
 * Complete security evaluation audit report.
 * Created by: Axel & M.B.A
 */
data class SecurityAuditReport(
    val overallScore: Int = 100,
    val dnsLeakStatus: LeakSeverity = LeakSeverity.SECURE,
    val webRtcLeakStatus: LeakSeverity = LeakSeverity.SECURE,
    val ipv6LeakStatus: LeakSeverity = LeakSeverity.SECURE,
    val detectedDnsServers: List<String> = emptyList(),
    val ipInfo: IpIntelligence = IpIntelligence(),
    val cqi: ConnectionQualityIndex = ConnectionQualityIndex(),
    val auditedAt: Long = System.currentTimeMillis()
)
