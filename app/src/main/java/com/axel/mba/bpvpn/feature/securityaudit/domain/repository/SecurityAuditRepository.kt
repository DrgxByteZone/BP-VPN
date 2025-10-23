package com.axel.mba.bpvpn.feature.securityaudit.domain.repository

import com.axel.mba.bpvpn.feature.securityaudit.model.IpIntelligence
import com.axel.mba.bpvpn.feature.securityaudit.model.SecurityAuditReport

/**
 * Repository interface for Security Auditor & Leak Sentinel.
 * Created by: Axel & M.B.A
 */
interface SecurityAuditRepository {
    suspend fun runFullAudit(currentIp: String, ispName: String): SecurityAuditReport
    suspend fun getIpIntelligence(ip: String): IpIntelligence
    suspend fun getLastAuditReport(): SecurityAuditReport?
}
