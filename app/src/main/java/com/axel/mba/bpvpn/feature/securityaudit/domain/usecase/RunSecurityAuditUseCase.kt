package com.axel.mba.bpvpn.feature.securityaudit.domain.usecase

import com.axel.mba.bpvpn.feature.securityaudit.domain.repository.SecurityAuditRepository
import com.axel.mba.bpvpn.feature.securityaudit.model.SecurityAuditReport

/**
 * UseCase to execute the full security audit.
 * Created by: Axel & M.B.A
 */
class RunSecurityAuditUseCase(private val repository: SecurityAuditRepository) {
    suspend operator fun invoke(currentIp: String, ispName: String): SecurityAuditReport {
        return repository.runFullAudit(currentIp, ispName)
    }
}
