package com.axel.mba.bpvpn.feature.securityaudit.engine

import com.axel.mba.bpvpn.feature.securityaudit.model.ConnectionQualityIndex
import com.axel.mba.bpvpn.feature.securityaudit.model.IpIntelligence
import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import com.axel.mba.bpvpn.feature.securityaudit.model.SecurityAuditReport

/**
 * High-performance orchestrator for privacy & leak security audits.
 * Created by: Axel & M.B.A
 */
class SecurityAuditEngine(
    private val dnsSentinel: DnsLeakSentinel = DnsLeakSentinel(),
    private val webRtcDetector: WebRtcLeakDetector = WebRtcLeakDetector(),
    private val ipv6Checker: Ipv6LeakChecker = Ipv6LeakChecker()
) {

    suspend fun executeAudit(
        currentIp: String,
        ispName: String,
        pingMs: Long = 20L,
        jitterMs: Long = 2L
    ): SecurityAuditReport {
        // 1. Audit DNS Leak
        val (dnsLeakStatus, detectedDns) = dnsSentinel.auditDnsLeak()

        // 2. Audit WebRTC STUN Leak
        val webRtcStatus = webRtcDetector.auditWebRtcLeak()

        // 3. Audit IPv6 Leak
        val ipv6Status = ipv6Checker.auditIpv6()

        // 4. IP Intelligence
        val isCloudflare = ispName.contains("Cloudflare", ignoreCase = true) || currentIp.startsWith("104.") || currentIp.startsWith("162.")
        val ipInfo = IpIntelligence(
            ip = currentIp,
            asn = if (isCloudflare) "AS13335" else "AS-Local",
            asOrganization = if (isCloudflare) "Cloudflare, Inc." else ispName,
            country = "Indonesia / Global",
            isVpnDetected = isCloudflare,
            threatScore = 0
        )

        // 5. Compute CQI
        val hasLeaks = dnsLeakStatus == LeakSeverity.CRITICAL || webRtcStatus == LeakSeverity.CRITICAL
        val cqi = ConnectionQualityIndex.calculate(pingMs, jitterMs, hasLeaks)

        // 6. Overall Security Score
        var score = 100
        if (dnsLeakStatus == LeakSeverity.WARNING) score -= 15
        if (dnsLeakStatus == LeakSeverity.CRITICAL) score -= 35
        if (webRtcStatus == LeakSeverity.CRITICAL) score -= 25
        if (ipv6Status == LeakSeverity.WARNING) score -= 10
        if (!isCloudflare) score -= 10

        val finalScore = score.coerceIn(10, 100)

        return SecurityAuditReport(
            overallScore = finalScore,
            dnsLeakStatus = dnsLeakStatus,
            webRtcLeakStatus = webRtcStatus,
            ipv6LeakStatus = ipv6Status,
            detectedDnsServers = detectedDns,
            ipInfo = ipInfo,
            cqi = cqi,
            auditedAt = System.currentTimeMillis()
        )
    }
}
