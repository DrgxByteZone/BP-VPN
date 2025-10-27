package com.axel.mba.bpvpn.feature.securityaudit.domain.usecase

import com.axel.mba.bpvpn.feature.securityaudit.domain.repository.SecurityAuditRepository
import com.axel.mba.bpvpn.feature.securityaudit.model.IpIntelligence

/**
 * UseCase to fetch IP intelligence data.
 * Created by: Axel & M.B.A
 */
class GetIpIntelligenceUseCase(private val repository: SecurityAuditRepository) {
    suspend operator fun invoke(ip: String): IpIntelligence {
        return repository.getIpIntelligence(ip)
    }
}
