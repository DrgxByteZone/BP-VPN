package com.axel.mba.bpvpn.feature.securityaudit.data.repository

import com.axel.mba.bpvpn.feature.securityaudit.data.db.SecurityAuditDao
import com.axel.mba.bpvpn.feature.securityaudit.data.db.SecurityAuditEntity
import com.axel.mba.bpvpn.feature.securityaudit.domain.repository.SecurityAuditRepository
import com.axel.mba.bpvpn.feature.securityaudit.engine.SecurityAuditEngine
import com.axel.mba.bpvpn.feature.securityaudit.model.IpIntelligence
import com.axel.mba.bpvpn.feature.securityaudit.model.LeakSeverity
import com.axel.mba.bpvpn.feature.securityaudit.model.SecurityAuditReport
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Implementation of SecurityAuditRepository.
 * Created by: Axel & M.B.A
 */
class SecurityAuditRepositoryImpl(
    private val dao: SecurityAuditDao,
    private val engine: SecurityAuditEngine = SecurityAuditEngine()
) : SecurityAuditRepository {

    override suspend fun runFullAudit(currentIp: String, ispName: String): SecurityAuditReport = withContext(Dispatchers.IO) {
        val report = engine.executeAudit(currentIp, ispName)
        dao.insert(
            SecurityAuditEntity(
                overallScore = report.overallScore,
                dnsLeakStatus = report.dnsLeakStatus.name,
                webRtcLeakStatus = report.webRtcLeakStatus.name,
                ipv6LeakStatus = report.ipv6LeakStatus.name,
                ipAddress = currentIp,
                ispName = ispName,
                auditedAt = report.auditedAt
            )
        )
        report
    }

    override suspend fun getIpIntelligence(ip: String): IpIntelligence = withContext(Dispatchers.IO) {
        val isCf = ip.startsWith("104.") || ip.startsWith("162.") || ip.startsWith("172.")
        IpIntelligence(
            ip = ip,
            asn = if (isCf) "AS13335" else "AS-Local",
            asOrganization = if (isCf) "Cloudflare, Inc." else "Local ISP",
            country = "Indonesia / Anycast",
            isVpnDetected = isCf,
            threatScore = 0
        )
    }

    override suspend fun getLastAuditReport(): SecurityAuditReport? = withContext(Dispatchers.IO) {
        dao.getLatestAudit()?.let {
            SecurityAuditReport(
                overallScore = it.overallScore,
                dnsLeakStatus = LeakSeverity.valueOf(it.dnsLeakStatus),
                webRtcLeakStatus = LeakSeverity.valueOf(it.webRtcLeakStatus),
                ipv6LeakStatus = LeakSeverity.valueOf(it.ipv6LeakStatus),
                ipInfo = IpIntelligence(ip = it.ipAddress, asOrganization = it.ispName),
                auditedAt = it.auditedAt
            )
        }
    }
}
